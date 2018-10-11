package com.github.ontio.governance;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.*;
import com.github.ontio.core.governance.PeerPoolItem;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.ontio.smartcontract.nativevm.abi.Struct;

import java.util.ArrayList;
import java.util.List;

public class Governance {
    private OntSdk sdk;
    private final String contractAddress = "0000000000000000000000000000000000000007";
    public Governance(OntSdk sdk){
        this.sdk = sdk;
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
    public String inputPeerPoolMap(Account account, PeerPoolMap peerPoolMap,Account payer, long gaslimit, long gasprice) throws Exception {
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

    public String inputConfig(Account account, Configuration configuration, Account payer, long gaslimit, long gasprice) throws Exception {
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
    public String inputSplitCurve(Account account, SplitCurve splitCurve, Account payer, long gaslimit, long gasprice) throws Exception {
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
}

