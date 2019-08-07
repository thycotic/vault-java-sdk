# Introduction 
A Java client for the the Thycotic DevOps Secrets Vault management solution.

This Java client implements functions for authentication and secrets management via the DevOps Secrets Vault REST API. It is compatible with Java 8 and up.


# Getting Started

1.	###### Installation 
    The client is available from Maven Central, for all Java build systems.
    
    ```
    Gradle:
    
    dependencies {
        implementation 'com.thycotic:devops-secrets-vault-sdk:1.1.0'
    }
    Maven:
    
    <dependency>
        <groupId>com.thycotic</groupId>
        <artifactId>devops-secrets-vault-sdk</artifactId>
        <version>1.1.0</version>
    </dependency>
    ```
    
2.	###### Initializing a client Instance
    
       Client Credential Authentication
       ```java
       ClientCredentialParameters clientParams = ClientCredentialParameters.
                create("tenant", "domain.com", "clientid", "clientsecret");
       IDevOpsSecretsVaultClient client = new BaseClient(clientParams);
       SecretService se = new BaseSecretService(client);
       ```
       
       AWS IAM Authentication using default AWS profile on machine
       ```java
       AwsParameters params = AwsParameters.createFromDefault("tenant", "domain.com");
       IDevOpsSecretsVaultClient client = new BaseClient(params);
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
