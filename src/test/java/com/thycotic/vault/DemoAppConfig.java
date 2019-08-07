package com.thycotic.vault;

import com.thycotic.vault.client.BaseClient;
import com.thycotic.vault.client.ClientCredentialParameters;
import com.thycotic.vault.client.IDevOpsSecretsVaultClient;
import com.thycotic.vault.secret.BaseSecretService;
import com.thycotic.vault.secret.SecretService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DemoAppConfig {
    @Bean
    public IDevOpsSecretsVaultClient client() {
        ClientCredentialParameters params = ClientCredentialParameters.
                create("tenantname", "secretsvaultcloud.com", "clientId", "clientSecret");
        BaseClient client = new BaseClient(params);
        client.overrideVaultUrl("http://localhost:3333");
        return client;
    }

    @Bean
    public SecretService secretService() {
        return new BaseSecretService(client());
    }
}
