package com.github.ontio.common.sidechain;

import sun.jvm.hotspot.debugger.Address;

public class InflationParam {
    public String sideChainId;
    public Address address;
    public long depositAdd;
    public long ongPoolAdd;
    public InflationParam(String sideChainId, Address address, long depositAdd, long ongPoolAdd){
        this.sideChainId = sideChainId;
        this.address = address;
        this.depositAdd = depositAdd;
        this.ongPoolAdd = ongPoolAdd;
    }
}
