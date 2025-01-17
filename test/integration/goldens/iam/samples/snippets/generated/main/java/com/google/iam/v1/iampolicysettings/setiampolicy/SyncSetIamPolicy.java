/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.iam.v1.samples;

// [START iam_v1_generated_iampolicysettings_setiampolicy_sync]
import com.google.iam.v1.IAMPolicySettings;
import java.time.Duration;

public class SyncSetIamPolicy {

  public static void main(String[] args) throws Exception {
    syncSetIamPolicy();
  }

  public static void syncSetIamPolicy() throws Exception {
    // This snippet has been automatically generated for illustrative purposes only.
    // It may require modifications to work in your environment.
    IAMPolicySettings.Builder iAMPolicySettingsBuilder = IAMPolicySettings.newBuilder();
    iAMPolicySettingsBuilder
        .setIamPolicySettings()
        .setRetrySettings(
            iAMPolicySettingsBuilder
                .setIamPolicySettings()
                .getRetrySettings()
                .toBuilder()
                .setTotalTimeout(Duration.ofSeconds(30))
                .build());
    IAMPolicySettings iAMPolicySettings = iAMPolicySettingsBuilder.build();
  }
}
// [END iam_v1_generated_iampolicysettings_setiampolicy_sync]
