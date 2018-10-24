package com.github.ontio.common.sidechain;

import sun.jvm.hotspot.debugger.Address;

public class QuitSideChainParam {
    public String sideChainID;
    public Address address;
    public QuitSideChainParam(String sideChainID, Address address){
        this.sideChainID = sideChainID;
        this.address = address;
    }

}
