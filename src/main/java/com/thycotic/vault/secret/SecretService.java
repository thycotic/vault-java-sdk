package com.thycotic.vault.secret;

import com.thycotic.vault.exceptions.DevOpsSecretsVaultException;

import java.util.Map;
import java.util.Set;

/**
 * Manage secrets within Thycotic DevOps Secrets Vault
 *
 * @author dsv@thycotic.com
 * @since 1.0.0
 */
public interface SecretService {

    /**
     * Retrieve secret that is stored in json at given path
     *
     * @param path
     * @return map of data
     * @throws DevOpsSecretsVaultException
     */
    Map<String, Object> getSecretMap(String path) throws DevOpsSecretsVaultException;

    /**
     * List paths that match the start of the given string
     *
     * @param start
     * @return List of valid paths
     * @throws DevOpsSecretsVaultException
     */
    Set<String> listSecretPaths(String start) throws DevOpsSecretsVaultException;

    /**
     * Create a secret given a provided path
     *
     * @param path
     * @param secret
     * @throws DevOpsSecretsVaultException
     */
    void createSecret(String path, SecretPushData secret) throws DevOpsSecretsVaultException;

    /**
     * Update secret at given path
     *
     * @param path
     * @param secret
     * @throws DevOpsSecretsVaultException
     */
    void updateSecret(String path, SecretPushData secret) throws DevOpsSecretsVaultException;

    /**
     * Delete secret given path
     *
     * @param path
     * @throws DevOpsSecretsVaultException
     */
    void deleteSecret(String path) throws DevOpsSecretsVaultException;
}
