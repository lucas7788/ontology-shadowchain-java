package com.github.ontio.server.base;

import java.util.List;

public class MsgInfo {
    public String chainType;//mainChain or sideChain
    public String nodeUrl;
    public String functionName;
    public String sideChainId;
    public String address;
    public int amount;

    public MsgInfo(String chainType,String nodeUrl,String functionName,String sideChainId,String address,int amount){
        this.chainType = chainType;
        this.nodeUrl = nodeUrl;
        this.functionName = functionName;
        this.sideChainId = sideChainId;
        this.address = address;
        this.amount = amount;
    }
}
