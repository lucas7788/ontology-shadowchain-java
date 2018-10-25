package com.github.ontio.common;

import java.util.List;

public class Config {
    public List<String> mainChainUrl;
    public List<String> shadowChainUrl;
    public String wallet;
    public List admin;
    public List superAdmin;
    public Config(){}

    public List<String> getMainChainUrl() {
        return mainChainUrl;
    }

    public void setMainChainUrl(List<String> mainChainUrl) {
        this.mainChainUrl = mainChainUrl;
    }

    public List<String> getShadowChainUrl() {
        return shadowChainUrl;
    }

    public void setShadowChainUrl(List<String> shadowChainUrl) {
        this.shadowChainUrl = shadowChainUrl;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public List getAdmin() {
        return admin;
    }

    public void setAdmin(List admin) {
        this.admin = admin;
    }

    public List getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(List superAdmin) {
        this.superAdmin = superAdmin;
    }

}
