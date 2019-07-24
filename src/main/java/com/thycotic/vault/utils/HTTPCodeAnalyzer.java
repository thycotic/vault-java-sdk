package com.thycotic.vault.utils;

import com.thycotic.vault.exceptions.DevOpsSecretsVaultAuthenticationException;
import com.thycotic.vault.exceptions.DevOpsSecretsVaultException;
import com.thycotic.vault.exceptions.DevOpsSecretsVaultServiceException;

public class HTTPCodeAnalyzer {
    public static void validateResponseCode(int code) throws DevOpsSecretsVaultException {
        switch (code) {
            case 200:
            case 201:
            case 204:
            case 404:
                return;
            case 500:
            case 502:
                throw new DevOpsSecretsVaultServiceException("DevOps Secrets Vault Service Error");
            case 401:
            case 403:
                throw new DevOpsSecretsVaultAuthenticationException("Not permitted to access this secret");
            default:
                throw new DevOpsSecretsVaultException(String.format("Received error code %d from service", code));
        }
    }
}
