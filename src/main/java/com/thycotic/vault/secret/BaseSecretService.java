package com.thycotic.vault.secret;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thycotic.vault.client.IDevOpsSecretsVaultClient;
import com.thycotic.vault.exceptions.DevOpsSecretsVaultException;
import com.thycotic.vault.utils.HTTPCodeAnalyzer;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Basic service implementation that calls Thycotic DevOps Secrets Vault API. Has the ability to be wired as a bean.
 *
 * @author dsv@thycotic.com
 * @since 1.0.0
 */
public class BaseSecretService implements SecretService {
    private IDevOpsSecretsVaultClient client;
    private String baseEndpoint;

    private static final String URI_PATH = "/v1/secrets";

    @Inject
    public BaseSecretService(IDevOpsSecretsVaultClient client) {
        this.client = client;
        this.baseEndpoint = client.getEndpoint() + URI_PATH;
    }


    public Map<String, Object> getSecretMap(String path) throws DevOpsSecretsVaultException {
        ObjectMapper mapper = getObjectMapper();

        path = checkPath(path);

        try {
            HttpURLConnection conn = getConnection(this.baseEndpoint + path, "GET");
            HTTPCodeAnalyzer.validateResponseCode(conn.getResponseCode());


            if (conn.getResponseCode() == 404) {
                return null;
            }
            SecretResponseData response = mapper.readValue(conn.getInputStream(), SecretResponseData.class);
            conn.disconnect();

            return response.getObjectData();
        } catch (IOException e) {
            throw new DevOpsSecretsVaultException("Error reading resource from service", e);
        }
    }

    public Set<String> listSecretPaths(String start) throws DevOpsSecretsVaultException {
        ObjectMapper mapper = getObjectMapper();

        start = checkPath(start);

        try {
            HttpURLConnection conn = getConnection(this.baseEndpoint + start + "::listpaths", "GET");
            HTTPCodeAnalyzer.validateResponseCode(conn.getResponseCode());

            if (conn.getResponseCode() == 404) {
                return Collections.emptySet();
            }

            SecretListResponseData response = mapper.readValue(conn.getInputStream(), SecretListResponseData.class);
            conn.disconnect();

            return response.getData();
        } catch (IOException e) {
            throw new DevOpsSecretsVaultException("Error reading resource from service", e);
        }
    }

    public void createSecret(String path, SecretPushData secret) throws DevOpsSecretsVaultException {
        ObjectMapper mapper = getObjectMapper();

        path = checkPath(path);

        try {
            String data = mapper.writeValueAsString(secret);

            HttpURLConnection conn = getConnection(this.baseEndpoint + path, "POST");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
            os.flush();

            HTTPCodeAnalyzer.validateResponseCode(conn.getResponseCode());

            conn.disconnect();
        } catch (IOException e) {
            throw new DevOpsSecretsVaultException("Error posting resource to service", e);
        }
    }

    public void updateSecret(String path, SecretPushData secret) throws DevOpsSecretsVaultException {
        ObjectMapper mapper = getObjectMapper();

        path = checkPath(path);

        try {
            String data = mapper.writeValueAsString(secret);

            HttpURLConnection conn = getConnection(this.baseEndpoint + path, "PUT");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
            os.flush();

            HTTPCodeAnalyzer.validateResponseCode(conn.getResponseCode());

            conn.disconnect();
        } catch (IOException e) {
            throw new DevOpsSecretsVaultException("Error updating resource using service", e);
        }
    }

    public void deleteSecret(String path) throws DevOpsSecretsVaultException {
        path = checkPath(path);

        try {
            HttpURLConnection conn = getConnection(this.baseEndpoint + path, "DELETE");
            conn.connect();

            HTTPCodeAnalyzer.validateResponseCode(conn.getResponseCode());

            conn.disconnect();
        } catch (IOException e) {
            throw new DevOpsSecretsVaultException("Error updating resource using service", e);
        }
    }

    private String checkPath(String path) {
        if (path.charAt(0) != '/') {
            path = "/" + path;
        }
        return path;
    }

    private HttpURLConnection getConnection(String urlString, String request) throws DevOpsSecretsVaultException, IOException {

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("Authorization", client.getAuthToken());
        conn.setRequestMethod(request);
        conn.setRequestProperty("Content-Type", "application/json");

        return conn;

    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

}
