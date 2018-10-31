package com.github.ontio.shadowchain;

import com.github.ontio.OntSdk;
import com.github.ontio.network.rpc.RpcClient;
import com.github.ontio.sdk.manager.ConnectMgr;
import com.github.ontio.shadowchain.smartcontract.governance.Governance;
import com.github.ontio.shadowchain.smartcontract.ongx.OngX;

public class ShadowChain {
    private RpcClient sideChainRpcClient;
    private Governance governance;
    private OngX ongX;
    private String sideChainUrl;

    public ShadowChain(OntSdk sdk, String sideChainUrl){
        this.sideChainUrl = sideChainUrl;
        sideChainRpcClient = new RpcClient(sideChainUrl);
        governance = new Governance(sdk, sideChainRpcClient);
        ongX = new OngX(sdk, sideChainRpcClient);
    }

    public void updateSideChainUrl(String nodeUrl){
        sideChainUrl = nodeUrl;
        sideChainRpcClient = new RpcClient(nodeUrl);
        ongX.setRpcUrl(nodeUrl);
        governance.setRpcUrl(nodeUrl);
    }

    public ConnectMgr getSideChainRpcClient() {
        return new ConnectMgr(sideChainUrl,"rpc");
    }

    public void setSideChainRpcClient(RpcClient sideChainRpcClient) {
        this.sideChainRpcClient = sideChainRpcClient;
    }

    public Governance getGovernance() {
        return governance;
    }

    public void setGovernance(Governance governance) {
        this.governance = governance;
    }

    public OngX getOngX() {
        return ongX;
    }

    public void setOngX(OngX ongX) {
        this.ongX = ongX;
    }

    public String getSideChainUrl() {
        return sideChainUrl;
    }
}
