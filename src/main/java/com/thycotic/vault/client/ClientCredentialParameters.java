package com.thycotic.vault.client;

/**
 * Parameters for authenticating with client credentials to the vault
 *
 *  @author dsv@thycotic.com
 *  @since 1.1.0
 */
public class ClientCredentialParameters extends BaseParameters {
    private String clientId;
    private String clientSecret;

    private ClientCredentialParameters(String tenant, String baseDomain, String clientId, String clientSecret) {
        super(tenant, baseDomain);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * @param tenant DSV tenant name
     * @param baseDomain DSV domain name
     * @param clientId clientid for authenticating to DSV
     * @param clientSecret clientsecret for authenticating to DSV
     * @return populated ClientCredentialParameters object
     */
    public static ClientCredentialParameters create(String tenant, String baseDomain, String clientId, String clientSecret) {
        return new ClientCredentialParameters(tenant, baseDomain, clientId, clientSecret);
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
