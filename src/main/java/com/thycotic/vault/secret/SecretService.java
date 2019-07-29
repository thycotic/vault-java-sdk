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
     * @param path path to secret
     * @return map of data
     * @throws DevOpsSecretsVaultException  Secrets Vault Exception
     */
    Map<String, Object> getSecretMap(String path) throws DevOpsSecretsVaultException;

    /**
     * List paths that match the start of the given string
     *
     * @param start  secret path starts
     * @return List of valid paths
     * @throws DevOpsSecretsVaultException Secrets Vault Exception
     */
    Set<String> listSecretPaths(String start) throws DevOpsSecretsVaultException;

    /**
     * Create a secret given a provided path
     *
     * @param path path to secret
     * @param secret secret to persist
     * @throws DevOpsSecretsVaultException Secrets Vault Exception
     */
    void createSecret(String path, SecretPushData secret) throws DevOpsSecretsVaultException;

    /**
     * Update secret at given path
     *
     * @param path path to secret
     * @param secret  secret to update
     * @throws DevOpsSecretsVaultException Secrets Vault Exception
     */
    void updateSecret(String path, SecretPushData secret) throws DevOpsSecretsVaultException;

    /**
     * Delete secret given path
     *
     * @param path path to secret
     * @throws DevOpsSecretsVaultException Secrets Vault Exception
     */
    void deleteSecret(String path) throws DevOpsSecretsVaultException;
}
