package com.thycotic.vault.secret;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.thycotic.vault.DemoAppConfig;
import com.thycotic.vault.client.AuthenticationResponse;
import com.thycotic.vault.exceptions.DevOpsSecretsVaultAuthenticationException;
import com.thycotic.vault.exceptions.DevOpsSecretsVaultException;
import com.thycotic.vault.exceptions.DevOpsSecretsVaultServiceException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = DemoAppConfig.class)
public class SecretServiceTests {
    @Autowired
    SecretService underTest;

    private ObjectMapper mapper = new ObjectMapper();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(3333));

    @Before
    public void init() throws Exception {
        stubFor(post(urlEqualTo("/v1/token"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getFakeToken())));
    }



    @Test
    public void TestGetSecretJSON() throws Exception {
        stubFor(get(urlEqualTo("/v1/secrets/SECRET"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"42b4e55a-1c22-40e8-9ed9-d5bc4e1dd326\", \"path\": \"secret\", \"type\": \"json\", \"version\": \"0\", \"unknownProperty\": \"this doesn't exist\", \"created\": \"2018-10-24T16:37:34Z\", \"lastModified\": \"2018-10-24T16:37:34Z\", \"createdBy\": \"users:joel\", \"lastModifiedBy\": \"users:joel\", \"attributes\": null, \"data\": {\"hello\": \"world\"} }")));

        Map<String, Object> result = underTest.getSecretMap("SECRET");
        assertThat(result.get("hello")).isEqualTo("world");

        result = underTest.getSecretMap("/SECRET");
        assertThat(result.get("hello")).isEqualTo("world");

       
    }



    @Test
    public void TestListSecrets() throws Exception {
        stubFor(get(urlEqualTo("/v1/secrets/s::listpaths"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"data\": [ \"secret\", \"super\" ] }")));


        Set<String> result = underTest.listSecretPaths("/s");
        assertThat(result).hasSize(2);
        assertThat(result).contains("secret");
    }

    @Test
    public void TestListSecrets_404() throws Exception {
        stubFor(get(urlEqualTo("/v1/secrets/a::listpaths"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(404)));

        Set<String> result = underTest.listSecretPaths("a");
        assertThat(result).isEmpty();
    }

    @Test(expected = DevOpsSecretsVaultException.class)
    public void TestListSecrets_Malformed() throws Exception {
        stubFor(get(urlEqualTo("/v1/secrets/a::listpaths"))
                .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        underTest.listSecretPaths("a");
    }

    @Test
    public void TestCreateSecret() throws Exception {
        stubFor(post(urlEqualTo("/v1/secrets/foo"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"c9a8a83b-23f2-46ac-824f-b0af298eb243\", \"path\": \"foo\", \"type\": \"string\", \"attributes\": null, \"created\": \"2018-10-24T16:37:34Z\", \"lastModified\": \"2018-10-24T16:37:34Z\", \"createdBy\": \"users:joel\", \"lastModifiedBy\": \"users:joel\", \"description\": \"\" }")));

        Map<String, Object> att = new HashMap<String, Object>();
        att.put("tag1", "1");
        
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("foo", "test123");
        
        SecretPushData sd = new SecretPushData();
        sd.setAttributes(att);
        sd.setData(data);
        sd.setId("7083988c-eeb8-4a09-9fe3-27240cf8225f");
        
        underTest.createSecret("/foo", sd);
        underTest.createSecret("foo", sd);

      verify(postRequestedFor(urlEqualTo("/v1/secrets/foo"))
               .withRequestBody(equalToJson("{\"id\":\"7083988c-eeb8-4a09-9fe3-27240cf8225f\",\"data\":{\"foo\":\"test123\"},\"description\":null,\"attributes\":{\"tag1\":\"1\"}}")));
    }

    @Test(expected = DevOpsSecretsVaultException.class)
    public void TestCreateSecret_Timeout() throws Exception {
        stubFor(post(urlEqualTo("/v1/secrets/a"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));
        
        Map<String, Object> att = new HashMap<String, Object>();
        att.put("tag1", "1");
        
        Map<String, Object> data = new HashMap<String, Object>();
        att.put("foo", "test123");
        
        SecretPushData sd = new SecretPushData();
        sd.setAttributes(att);
        sd.setData(data);
        sd.setId("7083988c-eeb8-4a09-9fe3-27240cf8225f");

        underTest.createSecret("a",sd);
    }

    @Test
    public void TestUpdateSecret() throws Exception {
        stubFor(put(urlEqualTo("/v1/secrets/foo"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"c9a8a83b-23f2-46ac-824f-b0af298eb243\", \"path\": \"foo\", \"type\": \"string\", \"attributes\": null, \"created\": \"2018-10-24T16:37:34Z\", \"lastModified\": \"2018-10-24T16:37:34Z\", \"createdBy\": \"users:joel\", \"lastModifiedBy\": \"users:joel\", \"description\": \"\" }")));

        Map<String, Object> att = new HashMap<String, Object>();
        att.put("tag1", "1");
        
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("foo", "test123");
        
        SecretPushData sd = new SecretPushData();
        sd.setAttributes(att);
        sd.setData(data);
        sd.setId("7083988c-eeb8-4a09-9fe3-27240cf8225f");
       
        underTest.updateSecret("/foo", sd);
        underTest.updateSecret("foo", sd);

        verify(2, putRequestedFor(urlEqualTo("/v1/secrets/foo"))
                .withRequestBody(equalToJson("{\"id\":\"7083988c-eeb8-4a09-9fe3-27240cf8225f\",\"data\":{\"foo\":\"test123\"},\"description\":null,\"attributes\":{\"tag1\":\"1\"}}")));
    }

    @Test(expected = DevOpsSecretsVaultException.class)
    public void TestUpdateSecret_Timeout() throws Exception {
        stubFor(put(urlEqualTo("/v1/secrets/a"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));
        Map<String, Object> att = new HashMap<String, Object>();
        att.put("tag1", "1");
        
        Map<String, Object> data = new HashMap<String, Object>();
        att.put("foo", "test123");
        
        SecretPushData b = new SecretPushData();
        b.setAttributes(att);
        b.setData(data);
        b.setId("7083988c-eeb8-4a09-9fe3-27240cf8225f");

        underTest.updateSecret("a", b);
    }

    @Test
    public void TestDeleteSecret() throws Exception {
        stubFor(delete(urlEqualTo("/v1/secrets/foo"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)));

        underTest.deleteSecret("/foo");
        underTest.deleteSecret("foo");

        verify(2, deleteRequestedFor(urlEqualTo("/v1/secrets/foo")));
    }

    @Test(expected = DevOpsSecretsVaultException.class)
    public void TestDeleteSecret_Timeout() throws Exception {
        stubFor(delete(urlEqualTo("/v1/secrets/a"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

        underTest.deleteSecret("a");
    }

    @Test
    public void TestErrorCodes() throws Exception {

        Map<Integer, Class> testValues = new HashMap<>();
        testValues.put(500, DevOpsSecretsVaultServiceException.class);
        testValues.put(502, DevOpsSecretsVaultServiceException.class);
        testValues.put(401, DevOpsSecretsVaultAuthenticationException.class);
        testValues.put(403, DevOpsSecretsVaultAuthenticationException.class);
        testValues.put(123, DevOpsSecretsVaultException.class);


        for (Integer val : testValues.keySet()) {
            stubFor(get(urlEqualTo("/v1/secrets/test"))
                    .withHeader("Content-Type", equalTo("application/json"))
                    .willReturn(aResponse()
                            .withStatus(val)));

            try {
                underTest.getSecretMap("test");
            } catch (Exception e) {
                assertThat(e).isInstanceOf(testValues.get(val));
            }
        }
    }

    private String getFakeToken() throws Exception {
        AuthenticationResponse resp = new AuthenticationResponse();
        resp.setAccessToken("foo");
        resp.setExpiresIn("122");
        resp.setRefreshToken("bar");
        resp.setTokenType("bearer");

        return mapper.writeValueAsString(resp);
    }
}
