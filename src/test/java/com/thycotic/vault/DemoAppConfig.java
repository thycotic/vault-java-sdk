package com.thycotic.vault;

import com.thycotic.vault.client.BaseClient;
import com.thycotic.vault.client.IDevOpsSecretsVaultClient;
import com.thycotic.vault.secret.BaseSecretService;
import com.thycotic.vault.secret.SecretService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DemoAppConfig {
    @Bean
    public IDevOpsSecretsVaultClient client() {
        BaseClient client = new BaseClient("tenantname", "username", "Password@1", "secrestvaultcloud.com");
        client.overrideVaultUrl("http://localhost:3333");
        return client;
    }

    @Bean
    public SecretService secretService() {
        return new BaseSecretService(client());
    }
}
