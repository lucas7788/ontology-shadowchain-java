package com.github.ontio.server.base;

import java.util.List;

public class MonitorParam {
    String contractAddress;
    String functionName;
    public MonitorParam(String contractAddress, String functionName){
        this.contractAddress = contractAddress;
        this.functionName = functionName;
    }
}
