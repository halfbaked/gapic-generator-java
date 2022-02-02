package com.google.storage.v2.samples;

import com.google.protobuf.Empty;
import com.google.storage.v2.CommonObjectRequestParams;
import com.google.storage.v2.CommonRequestParams;
import com.google.storage.v2.DeleteObjectRequest;
import com.google.storage.v2.StorageClient;

public class DeleteObjectDeleteObjectRequestRequest {

  public static void main(String[] args) throws Exception {
    deleteObjectDeleteObjectRequestRequest();
  }

  public static void deleteObjectDeleteObjectRequestRequest() throws Exception {
    try (StorageClient storageClient = StorageClient.create()) {
      DeleteObjectRequest request =
          DeleteObjectRequest.newBuilder()
              .setBucket("bucket-1378203158")
              .setObject("object-1023368385")
              .setUploadId("uploadId1563990780")
              .setGeneration(305703192)
              .setIfGenerationMatch(-1086241088)
              .setIfGenerationNotMatch(1475720404)
              .setIfMetagenerationMatch(1043427781)
              .setIfMetagenerationNotMatch(1025430873)
              .setCommonObjectRequestParams(CommonObjectRequestParams.newBuilder().build())
              .setCommonRequestParams(CommonRequestParams.newBuilder().build())
              .build();
      storageClient.deleteObject(request);
    }
  }
}
