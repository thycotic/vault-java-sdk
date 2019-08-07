package com.thycotic.vault.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thycotic.vault.exceptions.DevOpsSecretsVaultException;
import com.thycotic.vault.utils.HTTPCodeAnalyzer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.utils.StringUtils;
import net.minidev.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Basic client implementation to retrieve credentials using auth endpoint
 */
public class BaseClient implements IDevOpsSecretsVaultClient {
    private String tenant, clientId, clientSecret, vaultUrl, roleArn, accessKey, secretKey, grantType;
    private final String TOKEN_ENDPOINT = "/v1/token";
    private final int STS_DURATION = 3600;
    private final String STS_BODY = "Action=GetCallerIdentity&Version=2011-06-15";

    private static final String VAULT_URL_BASE = "https://%s.%s";

    /**
     * Create client using client credentials
     * Kept for binary compatibility with 1.0.1
     *
     * @param tenant       tenant name
     * @param clientId     client id specific  to user
     * @param clientSecret client secret specific to user
     * @param baseDomain   url for domain like qa or prod domain
     */
    public BaseClient(String tenant, String clientId, String clientSecret, String baseDomain) {
        this.tenant = tenant;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.grantType = "client_credentials";
        this.vaultUrl = String.format(VAULT_URL_BASE, tenant, baseDomain);
    }

    /**
     * Create client using aws iam credentials. If no credentials are specified they will be pulled from the default provider
     *
     * @param awsParameters      parameter object for aws iam authentication
     */
    public BaseClient(AwsParameters awsParameters) {
        this.tenant = awsParameters.getTenant();
        this.roleArn = awsParameters.getRoleArn();
        this.accessKey = awsParameters.getAccessKey();
        this.secretKey = awsParameters.getSecretKey();
        this.grantType = "aws_iam";
        this.vaultUrl = String.format(VAULT_URL_BASE, tenant, awsParameters.getBaseDomain());
    }

    /**
     * Create client using client credentials
     *
     * @param clientCredentialParameters      parameter object for client credential authentication
     */
    public BaseClient(ClientCredentialParameters clientCredentialParameters) {
        this.tenant = clientCredentialParameters.getTenant();
        this.clientId = clientCredentialParameters.getClientId();
        this.clientSecret = clientCredentialParameters.getClientSecret();
        this.grantType = "client_credentials";
        this.vaultUrl = String.format(VAULT_URL_BASE, tenant, clientCredentialParameters.getBaseDomain());
    }

    public void overrideVaultUrl(String url) {
        this.vaultUrl = url;
    }

    //TODO caching?
    public String getAuthToken() throws DevOpsSecretsVaultException {
        try {
            if (this.grantType == "client_credentials") {
                AuthenticationResponse response = getAuthenticationResponse(new AuthenticationBody(clientId, clientSecret));
                return response.getAccessToken();
            }
            if (this.grantType == "aws_iam") {
                return getAuthTokenAwsIam(this.roleArn, this.accessKey, this.secretKey);
            }
            throw new DevOpsSecretsVaultException("Client settings not configured");
        } catch (IOException e) {
            throw new DevOpsSecretsVaultException("Error authenticating with vault service", e);
        }
    }

    private String getAuthTokenAwsIam(String roleArn, String accessKey, String secretKey) throws DevOpsSecretsVaultException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            String sessionToken = "";
            AwsCredentials creds = null;
            if (!StringUtils.isBlank(accessKey) && !StringUtils.isBlank(secretKey)) {
                creds = AwsBasicCredentials.create(accessKey, secretKey);
            }
            if (StringUtils.isBlank(accessKey) && StringUtils.isBlank(secretKey)) {
                creds = DefaultCredentialsProvider.create().resolveCredentials();
            }

            DefaultAwsRegionProviderChain regionProviderChain = new DefaultAwsRegionProviderChain();
            Region region = null;
            try {
                region = regionProviderChain.getRegion();
            } catch (SdkClientException ex) {
                //add logging
            }
            if (region == null) {
                region = Region.US_EAST_1;
            }

            String endpoint = "sts.amazonaws.com";
            if (region != Region.US_EAST_1) {
                endpoint = String.format("sts.%s.amazonaws.com", region.toString());
            }

            // If the role arn is set, try to assume the role and set the creds to that role
            if (!StringUtils.isBlank(roleArn)) {
                StsClient stsClient = StsClient.builder()
                        .credentialsProvider(StaticCredentialsProvider.create(creds))
                        .build();

                AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                        .durationSeconds(STS_DURATION)
                        .roleArn(roleArn)
                        .roleSessionName(tenant)
                        .build();

                AssumeRoleResponse assumeRoleResponse = stsClient.assumeRole(assumeRoleRequest);
                creds = AwsBasicCredentials.create(assumeRoleResponse.credentials().accessKeyId(),
                        assumeRoleResponse.credentials().secretAccessKey());
                sessionToken = assumeRoleResponse.credentials().sessionToken();
            }


            Aws4SignerParams params = Aws4SignerParams.builder()
                    .signingRegion(region)
                    .awsCredentials(creds)
                    .signingName("sts")
                    .build();

            SdkHttpFullRequest.Builder requestBuilder = SdkHttpFullRequest.builder()
                    .host(endpoint)
                    .method(SdkHttpMethod.POST)
                    .contentStreamProvider(() -> new ByteArrayInputStream(STS_BODY.getBytes(StandardCharsets.UTF_8)))
                    .appendHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                    .appendHeader("Host", endpoint)
                    .protocol("https");

            if (!StringUtils.isBlank(sessionToken)) {
                requestBuilder.appendHeader("X-Amz-Security-Token", sessionToken);
            }
            SdkHttpFullRequest fullRequest = requestBuilder.build();

            SdkHttpFullRequest result = Aws4Signer.create().sign(fullRequest, params);
            Map<String, List<String>> signedHeaders = new HashMap<String, List<String>>();
            for (Map.Entry<String, List<String>> h : result.headers().entrySet()) {
                signedHeaders.put(h.getKey(), h.getValue());
            }

            JSONObject json = new JSONObject();
            json.putAll(signedHeaders);

            String headers = Base64.getEncoder().encodeToString(json.toString().getBytes(StandardCharsets.UTF_8));
            String body = Base64.getEncoder().encodeToString(STS_BODY.getBytes(StandardCharsets.UTF_8));

            AuthenticationBody auth = new AuthenticationBody("aws_iam");
            auth.setAwsHeaders(headers);
            auth.setAwsBody(body);

            AuthenticationResponse response = getAuthenticationResponse(auth);

            return response.getAccessToken();
        } catch (IOException e) {
            throw new DevOpsSecretsVaultException("Error authenticating with vault service", e);
        }
    }

    private AuthenticationResponse getAuthenticationResponse(AuthenticationBody auth) throws IOException, DevOpsSecretsVaultException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        String authString = mapper.writeValueAsString(auth);

        URL authEndpoint = new URL(this.vaultUrl + TOKEN_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) authEndpoint.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        OutputStream os = conn.getOutputStream();
        os.write(authString.getBytes());
        os.flush();

        HTTPCodeAnalyzer.validateResponseCode(conn.getResponseCode());
        AuthenticationResponse response = mapper.readValue(conn.getInputStream(), AuthenticationResponse.class);
        conn.disconnect();
        return response;
    }

    public String getEndpoint() {
        return vaultUrl;
    }
}
