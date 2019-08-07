package com.thycotic.vault.client;

/**
 * Common properties used across all types of authentication parameters when creating a client
 *
 * @author dsv@thycotic.com
 * @since 1.1.0
 */
public abstract class BaseParameters {
    private String tenant;
    private String baseDomain;

    public BaseParameters(String tenant, String baseDomain) {
        this.tenant = tenant;
        this.baseDomain = baseDomain;
    }

    public String getBaseDomain() {
        return baseDomain;
    }
    public String getTenant() {
        return tenant;
    }
}
