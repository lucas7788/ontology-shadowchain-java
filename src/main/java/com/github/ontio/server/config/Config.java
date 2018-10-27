package com.github.ontio.server.config;

import java.util.List;
import java.util.Map;

public class Config {
    public List<String> mainChainUrl;
    public List<String> shadowChainUrl;
    public String wallet;
    public int mainChainHeight;
    public int sideChainHeight;
    public Map<String, String> admin;
    public List superAdmin;
    public long gasLimit;
    public long gasPrice;
    public Config(){}



    public int getMainChainHeight() {
        return mainChainHeight;
    }

    public void setMainChainHeight(int mainChainHeight) {
        this.mainChainHeight = mainChainHeight;
    }

    public int getSideChainHeight() {
        return sideChainHeight;
    }

    public void setSideChainHeight(int sideChainHeight) {
        this.sideChainHeight = sideChainHeight;
    }

    public long getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(long gasLimit) {
        this.gasLimit = gasLimit;
    }

    public long getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(long gasPrice) {
        this.gasPrice = gasPrice;
    }

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

    public Map<String, String> getAdmin() {
        return admin;
    }

    public void setAdmin(Map<String,String> admin) {
        this.admin = admin;
    }

    public List getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(List superAdmin) {
        this.superAdmin = superAdmin;
    }

}
