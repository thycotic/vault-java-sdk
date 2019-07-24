package com.thycotic.vault.secret;

import java.util.Set;

public class SecretListResponseData {
    private Set<String> data;

    public Set<String> getData() {
        return data;
    }

    public void setData(Set<String> data) {
        this.data = data;
    }
}
