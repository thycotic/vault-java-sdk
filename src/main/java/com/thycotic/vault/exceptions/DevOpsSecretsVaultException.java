package com.thycotic.vault.exceptions;

public class DevOpsSecretsVaultException extends Exception {
    public DevOpsSecretsVaultException(String message) {
        super(message);
    }

    public DevOpsSecretsVaultException(String message, Throwable e) {
        super(message, e);
    }
}
