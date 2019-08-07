package example;

import java.util.HashMap;
import java.util.Map;

import com.thycotic.vault.client.AwsParameters;
import com.thycotic.vault.client.BaseClient;
import com.thycotic.vault.client.ClientCredentialParameters;
import com.thycotic.vault.client.IDevOpsSecretsVaultClient;
import com.thycotic.vault.exceptions.DevOpsSecretsVaultException;
import com.thycotic.vault.secret.BaseSecretService;
import com.thycotic.vault.secret.SecretPushData;
import com.thycotic.vault.secret.SecretService;

public class example {

    public static void main(String[] args) throws DevOpsSecretsVaultException {
        ClientCredentialParameters clientParams = ClientCredentialParameters.
                create("tenant", "secretsvaultcloud.com", "3f29-8ade-da4dea913e66", "RA8mWU8XpFOMowM");
        IDevOpsSecretsVaultClient client = new BaseClient(clientParams);
        //

        SecretService se = new BaseSecretService(client);


        //Delete operation
        se.deleteSecret("test04");

        Map<String, Object> att = new HashMap<String, Object>();
        att.put("tag1", "1");

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("foo", "test123");

        SecretPushData sd = new SecretPushData();
        sd.setAttributes(att);
        sd.setData(data);
        sd.setId("7083988c-eeb8-4a09-9fe3-27240cf8225f");

        //Write operation
        se.createSecret("test04", sd);

        System.out.println("*******Created************");
        //Json Response
        Map<String, Object> results = se.getSecretMap("/test04");
        results.forEach((key, value) -> System.out.println(key + ":" + value));


        //Update operation
        data.put("foo2", "test000");
        sd.setData(data);


        se.updateSecret("test04", sd);

        System.out.println("*******Updated************");
        //Json Response
        Map<String, Object> results2 = se.getSecretMap("/test04");
        results2.forEach((key, value) -> System.out.println(key + ":" + value));

        //Delete operation
        se.deleteSecret("test04");


    }

}
