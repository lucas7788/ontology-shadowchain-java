package com.github.ontio.shadowchain.smartcontract.governance;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.*;
import com.github.ontio.core.governance.*;
import com.github.ontio.core.sidechaingovernance.NodeToSideChainParams;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.utils;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.network.rpc.RpcClient;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.shadowexception.ShadowErrorCode;
import com.github.ontio.shadowexception.ShadowException;
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
    private String SIDE_CHAIN_ID = "sideChainID";
    private RpcClient rpcClient;
    private final String contractAddress = "0000000000000000000000000000000000000007";

    public Governance(OntSdk sdk, RpcClient rpcClient){
        this.sdk = sdk;
        this.rpcClient = rpcClient;
    }
    public void setRpcUrl(String rpcUrl){
        this.rpcClient = new RpcClient(rpcUrl);
    }

    public String getSideChainId() throws ConnectorException, IOException, ShadowException {
        if(rpcClient == null){
            throw new ShadowException(ShadowErrorCode.OtherError("rpcClient is null"));
        }
        String res = rpcClient.getStorage(Helper.reverse(contractAddress), Helper.toHexString(SIDE_CHAIN_ID.getBytes()));
        return new String(Helper.hexToBytes(res));
    }

    public GovernanceView getGovernanceView() throws SDKException, ConnectorException, IOException, ShadowException {
        if(rpcClient == null){
            throw new ShadowException(ShadowErrorCode.OtherError("rpcClient is null"));
        }
        String view = rpcClient.getStorage(Helper.reverse(contractAddress),Helper.toHexString("governanceView".getBytes()));
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

    public Map getPeerPoolMap() throws ConnectorException, IOException, ShadowException {
        if(rpcClient == null){
            throw new ShadowException(ShadowErrorCode.OtherError("rpcClient is null"));
        }
        byte[] peerPoolBytes = "peerPool".getBytes();
        String value = rpcClient.getStorage(Helper.reverse(contractAddress), Helper.toHexString(peerPoolBytes));
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
        if(rpcClient == null){
            throw new ShadowException(ShadowErrorCode.OtherError("rpcClient is null"));
        }
        String res = rpcClient.getStorage(Helper.reverse(contractAddress), Helper.toHexString("splitCurve".getBytes()));
        SplitCurve curve = new SplitCurve();
        ByteArrayInputStream in = new ByteArrayInputStream(Helper.hexToBytes(res));
        BinaryReader reader = new BinaryReader(in);
        curve.deserialize(reader);
        return curve;
    }

    public Configuration getConfiguration() throws ConnectorException, IOException, ShadowException {
        if(rpcClient == null){
            throw new ShadowException(ShadowErrorCode.OtherError("rpcClient is null"));
        }
        String res = rpcClient.getStorage(Helper.reverse(contractAddress), Helper.toHexString("vbftConfig".getBytes()));
        if (res == null) {
            return null;
        } else {
            Configuration configuration = new Configuration();
            ByteArrayInputStream in = new ByteArrayInputStream(Helper.hexToBytes(res));
            BinaryReader reader = new BinaryReader(in);
            configuration.deserialize(reader);
            return configuration;
        }
    }

    public GlobalParam getGlobalParam() throws ConnectorException, IOException, ShadowException {
        if(rpcClient == null){
            throw new ShadowException(ShadowErrorCode.OtherError("rpcClient is null"));
        }
        String res = rpcClient.getStorage(Helper.reverse(contractAddress), Helper.toHexString(GLOBAL_PARAM.getBytes()));
        ByteArrayInputStream in = new ByteArrayInputStream(Helper.hexToBytes(res));
        BinaryReader reader = new BinaryReader(in);
        GlobalParam param = new GlobalParam();
        param.deserialize(reader);
        return param;
    }

    public String commitDpos(Account account, String sideChainData, Account payer, long gaslimit, long gasprice) throws Exception {
        if(account == null || payer == null || gaslimit < 0|| gasprice < 0){
            throw new SDKException(ErrorCode.OtherError("parameter is wrong"));
        }
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)),"commitDpos",
                Helper.hexToBytes(sideChainData),payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payer)){
            sdk.addSign(tx,payer);
        }
        rpcClient.sendRawTransaction(tx.toHexString());
        return tx.hash().toString();
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
        rpcClient.sendRawTransaction(tx.toHexString());
        return tx.hash().toString();
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
        rpcClient.sendRawTransaction(tx.toHexString());
        return tx.hash().toString();
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
        rpcClient.sendRawTransaction(tx.toHexString());
        return tx.hash().toString();
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
        rpcClient.sendRawTransaction(tx.toHexString());
        return tx.hash().toString();
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
        rpcClient.sendRawTransaction(tx.toHexString());
        return tx.hash().toString();
    }
}

