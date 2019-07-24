package example;

import java.util.HashMap;
import java.util.Map;

import com.thycotic.vault.client.BaseClient;
import com.thycotic.vault.client.IDevOpsSecretsVaultClient;
import com.thycotic.vault.exceptions.DevOpsSecretsVaultException;
import com.thycotic.vault.secret.BaseSecretService;
import com.thycotic.vault.secret.SecretPushData;
import com.thycotic.vault.secret.SecretService;

public class example {

	public static void main(String[] args) throws DevOpsSecretsVaultException {
			IDevOpsSecretsVaultClient client = new BaseClient("testtenant", "d8d4be3b-3f29-4a31-8ade-da4dea913e66", "RA8mWU8Xpz7yIgUgMXeQQHFJRlBVY1ple-2apYOMowM", "devbambe.com");
		   //
		   SecretService se = new BaseSecretService(client);
		   
		   
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
		   
		   
	}

}
