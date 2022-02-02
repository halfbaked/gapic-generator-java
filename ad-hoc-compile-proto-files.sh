#!/bin/sh

set -o errexit

# Some auto-generated Java proto library code for .proto files are not published anywhere,
# so currently, we need to build them using `java_proto_library()` to generate .jar files.
#
# Many of these are needed due to our usage in tests.
#
# 1. from grpc/grpc-proto: service_config.proto
#
# 2. from googleapis/googleapis:
#    - gapic_metadata.proto
#    - common_resources.proto
#    - pubsub.proto (v2)
#    - logging.proto (v1)
#
# 2. local testdata .protos in src/test/java/com/google/api/generator/gapic/testdata:
#    - deprecated_service: deprecated_service.proto
#    - bookshop: bookshop.proto
#    - showcase: compliance.proto, echo.proto, identity.proto, testing.proto and messaging.proto
#    - testgapic: bad_message_resname_def.proto, locker.proto, and nested_message.proto
#    - explicit_dynamic_routing_headers_testing:
#       - routing_rule_parser_testing.proto
#       - explicit_dynamic_routing_header_testing.proto

bazel build service_config_java_proto
bazel build @com_google_googleapis//gapic/metadata:metadata_java_proto
bazel build src/test/java/com/google/api/generator/gapic/composer:common_resources_java_proto
bazel build @com_google_googleapis//google/pubsub/v1:pubsub_java_proto
bazel build @com_google_googleapis//google/logging/v2:logging_java_proto

bazel build src/test/java/com/google/api/generator/gapic/testdata:deprecated_service_java_proto
bazel build src/test/java/com/google/api/generator/gapic/testdata:bookshop_java_proto
bazel build src/test/java/com/google/api/generator/gapic/testdata:showcase_java_proto
bazel build src/test/java/com/google/api/generator/gapic/testdata:testgapic_java_proto
bazel build src/test/java/com/google/api/generator/gapic/testdata:explicit_dynamic_routing_headers_testing_java_proto


mvn install:install-file -Dfile=bazel-bin/external/io_grpc_proto/libservice_config_proto-speed.jar \
    -DgroupId=io.grpc -DartifactId=serviceconfig-proto -Dversion=0.0.0 -Dpackaging=jar


mvn install:install-file -Dfile=bazel-bin/external/com_google_googleapis/gapic/metadata/libmetadata_proto-speed.jar \
    -DgroupId=com.google.api -DartifactId=metadata-proto -Dversion=0.0.0 -Dpackaging=jar

mvn install:install-file -Dfile=bazel-bin/external/com_google_googleapis/google/cloud/libcommon_resources_proto-speed.jar \
    -DgroupId=com.google.api -DartifactId=common-resources-proto -Dversion=0.0.0 -Dpackaging=jar

mvn install:install-file -Dfile=bazel-bin/external/com_google_googleapis/google/pubsub/v1/libpubsub_proto-speed.jar \
    -DgroupId=com.google.api -DartifactId=pubsub-proto -Dversion=0.0.0 -Dpackaging=jar

mvn install:install-file -Dfile=bazel-bin/external/com_google_googleapis/google/logging/v2/liblogging_proto-speed.jar \
    -DgroupId=com.google.api -DartifactId=logging-proto -Dversion=0.0.0 -Dpackaging=jar


mvn install:install-file -Dfile=bazel-bin/src/test/java/com/google/api/generator/gapic/testdata/libdeprecated_service_proto-speed.jar \
    -DgroupId=testdata -DartifactId=deprecated-service-proto -Dversion=0.0.0 -Dpackaging=jar

mvn install:install-file -Dfile=bazel-bin/src/test/java/com/google/api/generator/gapic/testdata/libbookshop_proto-speed.jar \
    -DgroupId=testdata -DartifactId=bookshop-proto -Dversion=0.0.0 -Dpackaging=jar

mvn install:install-file -Dfile=bazel-bin/src/test/java/com/google/api/generator/gapic/testdata/libshowcase_proto-speed.jar \
    -DgroupId=testdata -DartifactId=showcase-proto -Dversion=0.0.0 -Dpackaging=jar

mvn install:install-file -Dfile=bazel-bin/src/test/java/com/google/api/generator/gapic/testdata/libtestgapic_proto-speed.jar \
    -DgroupId=testdata -DartifactId=testgapic-proto -Dversion=0.0.0 -Dpackaging=jar

mvn install:install-file -Dfile=bazel-bin//src/test/java/com/google/api/generator/gapic/testdata/libexplicit_dynamic_routing_headers_testing_proto-speed.jar \
    -DgroupId=testdata -DartifactId=explicit-dynamic-routing-headers-testing-proto -Dversion=0.0.0 -Dpackaging=jar