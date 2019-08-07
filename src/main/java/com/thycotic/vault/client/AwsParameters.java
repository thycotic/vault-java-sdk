package com.thycotic.vault.client;

/**
 * Parameters for authenticating with AWS credentials to the vault
 * If no AWS specific parameters are set the SDK will attempt to authenticate using the default aws profile
 *
 * @author dsv@thycotic.com
 * @since 1.1.0
 */
public class AwsParameters extends BaseParameters {
    private String roleArn;
    private String accessKey;
    private String secretKey;

    private AwsParameters(String tenant, String baseDomain, String roleArn, String accessKey, String secretKey) {
        super(tenant, baseDomain);
        this.roleArn = roleArn;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * @param tenant DSV tenant name
     * @param baseDomain DSV domain name
     * @return new populated AWS Parameters object. When authenticating the default AWS profile will be used
     */
    public static AwsParameters createFromDefault(String tenant, String baseDomain) {
        return new AwsParameters(tenant, baseDomain, "", "", "");
    }

    /**
     * When authenticating the SDK will attempt to assume the target role using the default AWS profile
     * @param tenant DSV tenant name
     * @param baseDomain DSV domain name
     * @param roleArn AWS IAM Role to Assume
     * @return new populated AWS Parameters object.
     */
    public static AwsParameters createFromDefaultWithRole(String tenant, String baseDomain, String roleArn) {
        return new AwsParameters(tenant, baseDomain, roleArn, "", "");
    }

    /**
     * When authenticating the SDK will use the specified access key and secret key. If the roleArn is specified, it
     * will be assumed using the specified keys.
     *
     * @param tenant DSV tenant name
     * @param baseDomain DSV domain name
     * @param roleArn AWS IAM Role to Assume
     * @param accessKey AWS access key
     * @param secretKey AWS secret key
     * @return new populated AWS Parameters object.
     */
    public static AwsParameters createFromCredentials(String tenant, String baseDomain, String roleArn, String accessKey, String secretKey) {
        return new AwsParameters(tenant, baseDomain, roleArn, accessKey, secretKey);
    }

    public String getRoleArn() {
        return roleArn;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
