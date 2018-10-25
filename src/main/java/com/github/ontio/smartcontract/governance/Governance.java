package com.github.ontio.smartcontract.governance;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.*;
import com.github.ontio.core.governance.*;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.utils;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.ontio.smartcontract.nativevm.abi.Struct;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Governance {
    private OntSdk sdk;
    private String SIDE_CHAIN_NODE_INFO = "sideChainNodeInfo";
    private String GLOBAL_PARAM  = "globalParam";
    private String SPLIT_CURVE       = "splitCurve";
    private final String contractAddress = "0000000000000000000000000000000000000007";

    public Governance(OntSdk sdk){
        this.sdk = sdk;
    }
    public void setRpcUrl(String rpcUrl){
        this.sdk.setRpc(rpcUrl);
    }

    public GovernanceView getGovernanceView() throws SDKException, ConnectorException, IOException {
        String view = sdk.getConnect().getStorage(Helper.reverse(contractAddress),Helper.toHexString("governanceView".getBytes()));
        if(view == null || view.equals("")){
            throw new SDKException(ErrorCode.OtherError("view is null"));
        }
        GovernanceView governanceView = new GovernanceView();
        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes(view));
        BinaryReader reader = new BinaryReader(bais);
        governanceView.view = (int)utils.readVarInt(reader);
        governanceView.height = (int)utils.readVarInt(reader);
        governanceView.txhash = new UInt256(reader.readVarBytes());
        return governanceView;
    }

    public Map getPeerPoolMap() throws ConnectorException, IOException, SDKException {
        byte[] peerPoolBytes = "peerPool".getBytes();
        String value = this.sdk.getConnect().getStorage(Helper.reverse(contractAddress), Helper.toHexString(peerPoolBytes));
        ByteArrayInputStream bais2 = new ByteArrayInputStream(Helper.hexToBytes(value));
        BinaryReader reader = new BinaryReader(bais2);
        int length = (int)utils.readVarInt(reader);
        Map<String, PeerPoolItem> peerPoolMap2 = new HashMap();
        for(int i = 0; i < length; ++i) {
            PeerPoolItem item = new PeerPoolItem();
            item.index = (int)utils.readVarInt(reader);
            item.peerPubkey = reader.readVarString();
            item.address = utils.readAddress(reader);
            item.status = (int)utils.readVarInt(reader);
            item.initPos = utils.readVarInt(reader);
            item.totalPos = utils.readVarInt(reader);
            peerPoolMap2.put(item.peerPubkey, item);
        }
        return peerPoolMap2;
    }
    public SplitCurve getSplitCurve() throws ConnectorException, IOException, ShadowException, SDKException {
        return sdk.nativevm().governance().getSplitCurve();
    }

    public Configuration getConfiguration() throws SDKException, ConnectorException, IOException {
        return sdk.nativevm().governance().getConfiguration();
    }

    public GlobalParam getGlobalParam() throws ConnectorException, IOException {
        String res = sdk.getConnect().getStorage(Helper.reverse(contractAddress), Helper.toHexString(GLOBAL_PARAM.getBytes()));
        ByteArrayInputStream in = new ByteArrayInputStream(Helper.hexToBytes(res));
        BinaryReader reader = new BinaryReader(in);
        GlobalParam param = new GlobalParam();
        param.deserialize(reader);
        return param;
    }

    /**
     *
     * @param account
     * @param peerPoolMap
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String inputPeerPoolMap(Account account, InputPeerPoolMapParam peerPoolMap, Account payer, long gaslimit, long gasprice) throws Exception {
        if(account == null || peerPoolMap == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(peerPoolMap.peerPoolMap.size());
        for(PeerPoolItem item : peerPoolMap.peerPoolMap.values()){
            struct.add(item.index, item.peerPubkey, item.address, item.status, item.initPos, item.totalPos);
        }
        struct.add(peerPoolMap.nodeInfoMap.size());
        for(NodeToSideChainParams params : peerPoolMap.nodeInfoMap.values()){
            struct.add(params.peerPubkey,params.address,params.sideChainId);
        }
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"inputPeerPoolMap",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payer)){
            sdk.addSign(tx,payer);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }



    public String inputConfig(Account account, Configuration configuration, Account payer, long gaslimit, long gasprice) throws Exception {
        if(account == null || configuration == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(configuration.N,configuration.C,configuration.K,configuration.L,configuration.BlockMsgDelay,configuration.HashMsgDelay,configuration.PeerHandshakeTimeout,configuration.MaxBlockChangeView);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"inputConfig",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payer)){
            sdk.addSign(tx,payer);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String inputGlobalParam(Account account, GlobalParam param, Account payer, long gaslimit, long gasprice) throws Exception {
        if(account == null || param == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(param.candidateFeeSplitNum, param.A, param.B,param.yita);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"inputGlobalParam",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payer)){
            sdk.addSign(tx,payer);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String inputSplitCurve(Account account, SplitCurve splitCurve, Account payer, long gaslimit, long gasprice) throws Exception {
        if(account == null || splitCurve == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(splitCurve.Yi.length);
        for (int i : splitCurve.Yi) {
            struct.add(i);
        }
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"inputSplitCurve",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payer)){
            sdk.addSign(tx,payer);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String inputGovernanceView(Account account, GovernanceView view, Account payer, long gaslimit, long gasprice) throws Exception {
        if(account == null || view == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(view.view, view.height, view.txhash);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"inputGovernanceView",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payer)){
            sdk.addSign(tx,payer);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
}

