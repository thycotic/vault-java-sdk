package com.thycotic.vault.utils;

import com.thycotic.vault.exceptions.DevOpsSecretsVaultAuthenticationException;
import com.thycotic.vault.exceptions.DevOpsSecretsVaultException;
import com.thycotic.vault.exceptions.DevOpsSecretsVaultServiceException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class HTTPCodeAnalyzerTests {

    @Test
    public void TestErrorCodes() {
        HTTPCodeAnalyzer c = new HTTPCodeAnalyzer();

        Map<Integer, Class> testValues = new HashMap<>();
        testValues.put(500, DevOpsSecretsVaultServiceException.class);
        testValues.put(502, DevOpsSecretsVaultServiceException.class);
        testValues.put(401, DevOpsSecretsVaultAuthenticationException.class);
        testValues.put(403, DevOpsSecretsVaultAuthenticationException.class);
        testValues.put(123, DevOpsSecretsVaultException.class);

        for (Integer val : testValues.keySet()) {

            try {
                HTTPCodeAnalyzer.validateResponseCode(val);
            } catch (Exception e) {
                assertThat(e).isInstanceOf(testValues.get(val));
            }
        }
    }
}
