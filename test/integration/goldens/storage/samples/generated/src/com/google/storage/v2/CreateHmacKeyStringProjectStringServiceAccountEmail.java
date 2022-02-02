package com.google.storage.v2.samples;

import com.google.storage.v2.CreateHmacKeyResponse;
import com.google.storage.v2.ProjectName;
import com.google.storage.v2.StorageClient;

public class CreateHmacKeyStringProjectStringServiceAccountEmail {

  public static void main(String[] args) throws Exception {
    createHmacKeyStringProjectStringServiceAccountEmail();
  }

  public static void createHmacKeyStringProjectStringServiceAccountEmail() throws Exception {
    try (StorageClient storageClient = StorageClient.create()) {
      String project = ProjectName.of("[PROJECT]").toString();
      String serviceAccountEmail = "serviceAccountEmail1825953988";
      CreateHmacKeyResponse response = storageClient.createHmacKey(project, serviceAccountEmail);
    }
  }
}
