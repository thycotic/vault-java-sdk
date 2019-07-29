# Introduction 
A Java client for the the Thycotic DevOps Secrets Vault management solution.

This Java client implements functions for authentication and secrets management via the DevOps Secrets Vault REST API. It is compatible with Java 8 and up.


# Getting Started

1.	###### Installation 
    The client is available from Maven Central, for all Java build systems.
    
    ```
    Gradle:
    
    dependencies {
        implementation 'com.thycotic:devops-secrets-vault-sdk:1.0.0'
    }
    Maven:
    
    <dependency>
        <groupId>com.thycotic</groupId>
        <artifactId>devops-secrets-vault-sdk</artifactId>
        <version>1.0.0</version>
    </dependency>
    ```
    
2.	###### Initializing a client Instance

       ```java
        IDevOpsSecretsVaultClient client = new BaseClient("tenant", "clientid", "clientsecret", "domain.com");
        SecretService se = new BaseSecretService(client);
       ```
            
3.	###### Usage 
    ```
       Map<String, Object> att = new HashMap<String, Object>();
       att.put("tag1", "1");
    
       Map<String, Object> data = new HashMap<String, Object>();
       data.put("foo", "test123");
    
       SecretPushData sd = new SecretPushData();
       sd.setAttributes(att);
       sd.setData(data);
       sd.setId("7083988c-eeb8-4a09-9fe3-27240cf8225f");
       
      //Write operation
       se.createSecret("test02", sd);
       
       System.out.println("*******Created************");
       //Json Response 
       Map<String, Object> results = se.getSecretMap("/test02");
       results.forEach((key, value) -> System.out.println(key + ":" + value));

       
       //Update operation 
       data.put("foo2", "test000");
       sd.setData(data);
       
  
       se.updateSecret("test02", sd);
       
       System.out.println("*******Updated************");
       //Json Response 
       Map<String, Object> results2 = se.getSecretMap("/test02");
       results2.forEach((key, value) -> System.out.println(key + ":" + value));

       //Delete operation 
        se.deleteSecret("test02");
    ```

##### License

Copyright [2019] [thycotic]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

