package com.thycotic.vault.client;

import com.thycotic.vault.exceptions.DevOpsSecretsVaultException;

/**
 * Provides the ability to authenticate to the DevOps Secrets Vault API.
 *
 * @author dsv@thycotic.com
 * @since 1.0.0
 */
public interface IDevOpsSecretsVaultClient {
    /**
     * Retrieve authentication token for use within other services.
     *
     * @return JWT Authentication token
     * @throws DevOpsSecretsVaultException
     */
    String getAuthToken() throws DevOpsSecretsVaultException;

    /**
     * Retrieve custom endpoint for tenant
     *
     * @return tenant endpoint
     */
    String getEndpoint();
}
