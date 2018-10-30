package com.github.ontio.server.base;


public class MsgInfo {
    public String chainType;//mainChain or sideChain
    public String nodeUrl;
    public String functionName;
    public String sideChainId;
    public String address;
    public int amount;
    public int blockHeight;

    public MsgInfo(){}

    public MsgInfo(String chainType,String nodeUrl,String functionName,String sideChainId,String address,int amount, int blockHeight){
        this.chainType = chainType;
        this.nodeUrl = nodeUrl;
        this.functionName = functionName;
        this.sideChainId = sideChainId;
        this.address = address;
        this.amount = amount;
        this.blockHeight = blockHeight;
    }

    public int getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(int blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getChainType() {
        return chainType;
    }

    public void setChainType(String chainType) {
        this.chainType = chainType;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getSideChainId() {
        return sideChainId;
    }

    public void setSideChainId(String sideChainId) {
        this.sideChainId = sideChainId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
