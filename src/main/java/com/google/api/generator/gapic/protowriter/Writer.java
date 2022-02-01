// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.api.generator.gapic.protowriter;

import com.google.api.generator.engine.ast.ClassDefinition;
import com.google.api.generator.engine.ast.PackageInfoDefinition;
import com.google.api.generator.engine.writer.JavaWriterVisitor;
import com.google.api.generator.gapic.composer.samplecode.ExecutableSampleComposer;
import com.google.api.generator.gapic.model.GapicClass;
import com.google.api.generator.gapic.model.GapicContext;
import com.google.api.generator.gapic.model.GapicPackageInfo;
import com.google.api.generator.gapic.model.Sample;
import com.google.protobuf.ByteString;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse;
import com.google.protobuf.util.JsonFormat;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class Writer {
  static class GapicWriterException extends RuntimeException {
    public GapicWriterException(String errorMessage) {
      super(errorMessage);
    }
  }

  public static CodeGeneratorResponse write(
      GapicContext context,
      List<GapicClass> clazzes,
      GapicPackageInfo gapicPackageInfo,
      String outputFilePath) {
    ByteString.Output output = ByteString.newOutput();
    JavaWriterVisitor codeWriter = new JavaWriterVisitor();
    JarOutputStream jos;
    try {
      jos = new JarOutputStream(output);
    } catch (IOException e) {
      throw new GapicWriterException(e.getMessage());
    }

    Set<String> gapicSamples = new HashSet<>();
    for (GapicClass gapicClazz : clazzes) {
      writeClazzSamples(
              gapicClazz,
              getSamplePackage(gapicClazz),
              writeClazz(gapicClazz, codeWriter, jos),
              jos,
              gapicSamples);
    }

    writeMetadataFile(context, writePackageInfo(gapicPackageInfo, codeWriter, jos), jos);

    try {
      jos.finish();
      jos.flush();
    } catch (IOException e) {
      throw new GapicWriterException(e.getMessage());
    }

    CodeGeneratorResponse.Builder response = CodeGeneratorResponse.newBuilder();
    response
        .setSupportedFeatures(CodeGeneratorResponse.Feature.FEATURE_PROTO3_OPTIONAL_VALUE)
        .addFileBuilder()
        .setName(outputFilePath)
        .setContentBytes(output.toByteString());
    return response.build();
  }

  private static String writeClazz(
      GapicClass gapicClazz, JavaWriterVisitor codeWriter, JarOutputStream jos) {
    ClassDefinition clazz = gapicClazz.classDefinition();

    clazz.accept(codeWriter);
    String code = codeWriter.write();
    codeWriter.clear();

    String path = getPath(clazz.packageString(), clazz.classIdentifier().name());
    String className = clazz.classIdentifier().name();
    JarEntry jarEntry = new JarEntry(String.format("%s/%s.java", path, className));
    try {
      jos.putNextEntry(jarEntry);
      jos.write(code.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new GapicWriterException(
          String.format(
              "Could not write code for class %s.%s: %s",
              clazz.packageString(), clazz.classIdentifier().name(), e.getMessage()));
    }
    return path;
  }

   static void writeClazzSamples(
      GapicClass gapicClazz, String pakkage, String clazzPath, JarOutputStream jos, Set<String> gapicSamples) {
    for (Sample sample : gapicClazz.samples()) {
      if (!gapicSamples.contains(sample.getName())) {
        writeExecutableSample(sample, pakkage, clazzPath, jos);
      }
      gapicSamples.add(sample.getName());
    }
  }

  private static void writeExecutableSample(
      Sample sample, String pakkage, String clazzPath, JarOutputStream jos) {
    JarEntry jarEntry =
        new JarEntry(String.format("samples/generated/%s/%s.java", clazzPath, sample.getName()));
    String executableSampleCode =
        ExecutableSampleComposer.createExecutableSample(sample, pakkage);
    try {
      jos.putNextEntry(jarEntry);
      jos.write(executableSampleCode.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new GapicWriterException(
          String.format(
              "Could not write sample code for %s/%s.: %s", clazzPath, sample.getName(), e.getMessage()));
    }
  }

  private static String writePackageInfo(
      GapicPackageInfo gapicPackageInfo, JavaWriterVisitor codeWriter, JarOutputStream jos) {
    PackageInfoDefinition packageInfo = gapicPackageInfo.packageInfo();
    packageInfo.accept(codeWriter);
    String code = codeWriter.write();
    codeWriter.clear();

    String packagePath = "src/main/java/" + packageInfo.pakkage().replaceAll("\\.", "/");
    JarEntry jarEntry = new JarEntry(String.format("%s/package-info.java", packagePath));
    try {
      jos.putNextEntry(jarEntry);
      jos.write(code.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new GapicWriterException("Could not write code for package-info.java");
    }
    return packagePath;
  }

  private static void writeMetadataFile(GapicContext context, String path, JarOutputStream jos) {
    if (context.gapicMetadataEnabled()) {
      JarEntry jarEntry = new JarEntry(String.format("%s/gapic_metadata.json", path));
      try {
        jos.putNextEntry(jarEntry);
        jos.write(
            JsonFormat.printer().print(context.gapicMetadata()).getBytes(StandardCharsets.UTF_8));
      } catch (IOException e) {
        throw new GapicWriterException("Could not write gapic_metadata.json");
      }
    }
  }

   static String getPath(String pakkage, String className) {
    String path = pakkage.replaceAll("\\.", "/");
    if (className.startsWith("Mock") || className.endsWith("Test")) {
      path = "src/test/java/" + path;
    } else {
      path = "src/main/java/" + path;
    }

    // Resource name helpers go into the protobuf package. Denote this with "proto/src/main/*".
    if (className.endsWith("Name")) {
      path = "proto/" + path;
    }
    return path;
  }

  public static String getSamplePackage(GapicClass gapicClazz){
    return gapicClazz.classDefinition().packageString().concat(".samples");
  }
}
