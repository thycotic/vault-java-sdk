package com.thycotic.vault.client;

import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.thycotic.vault.DemoAppConfig;
import com.thycotic.vault.exceptions.DevOpsSecretsVaultException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = DemoAppConfig.class)
public class BaseClientTests {
    @Autowired
    IDevOpsSecretsVaultClient underTest;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(3333));

    @Test(expected = DevOpsSecretsVaultException.class)
    public void TestBadResponse() throws Exception {
        stubFor(post(urlEqualTo("/v1/token"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        underTest.getAuthToken();
    }
}
