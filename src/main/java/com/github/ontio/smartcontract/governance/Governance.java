package com.github.ontio.smartcontract.governance;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.*;
import com.github.ontio.core.governance.*;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.ontio.smartcontract.nativevm.abi.Struct;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
    public void setNodeUrl(String nodeUrl){
        this.sdk.setRpc(nodeUrl);
    }

    public InputPeerPoolMapParam getInputPeerPoolMapParam(String sideChainId) throws ConnectorException, IOException, SDKException {
        Map peerPoolMap = sdk.nativevm().governance().getPeerPoolMap();
        byte[] sideChainIdBytes = sideChainId.getBytes();
        byte[] sideChainNodeInfoBytes = SIDE_CHAIN_NODE_INFO.getBytes();
        byte[] key = new byte[sideChainIdBytes.length + sideChainNodeInfoBytes.length];
        System.arraycopy(sideChainNodeInfoBytes,0, key,0,sideChainNodeInfoBytes.length);
        System.arraycopy(sideChainIdBytes,0, key,sideChainNodeInfoBytes.length,sideChainIdBytes.length);
        String resNode = sdk.getConnect().getStorage(contractAddress, Helper.toHexString(key));
        NodeToSideChainParams params = new NodeToSideChainParams();
        ByteArrayInputStream in = new ByteArrayInputStream(Helper.hexToBytes(resNode));
        BinaryReader reader = new BinaryReader(in);
        params.deserialize(reader);
        return null;
    }
    public SplitCurve getSplitCurve() throws ConnectorException, IOException, ShadowException {
        String res = sdk.getConnect().getStorage(contractAddress, Helper.toHexString(SPLIT_CURVE.getBytes()));
        if(res==null || res.equals("")){
            throw new ShadowException(ShadowErrorCode.OtherError("splitCurve is null"));
        }
        SplitCurve curve = new SplitCurve();
        ByteArrayInputStream in = new ByteArrayInputStream(Helper.hexToBytes(res));
        BinaryReader reader = new BinaryReader(in);
        curve.deserialize(reader);
        return curve;
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

    public Configuration getConfiguration() throws SDKException, ConnectorException, IOException {
        return sdk.nativevm().governance().getConfiguration();
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
    public GlobalParam getGlobalParam() throws SDKException, ConnectorException, IOException {
        String res = sdk.getConnect().getStorage(Helper.reverse(contractAddress), Helper.toHexString(GLOBAL_PARAM.getBytes()));
        ByteArrayInputStream in = new ByteArrayInputStream(Helper.hexToBytes(res));
        BinaryReader reader = new BinaryReader(in);
        GlobalParam param = new GlobalParam();
        param.deserialize(reader);
        return param;
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

