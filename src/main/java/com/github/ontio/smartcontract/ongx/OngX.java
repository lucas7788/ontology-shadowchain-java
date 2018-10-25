package com.github.ontio.smartcontract.ongx;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.common.ongx.Swap;
import com.github.ontio.core.asset.State;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.ontio.smartcontract.nativevm.abi.Struct;

import java.util.ArrayList;
import java.util.List;

public class OngX {
    private OntSdk sdk;
    private final String ongContract = "0000000000000000000000000000000000000002";

    public OngX(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setRpcUrl(String rpcUrl){
        sdk.setRpc(rpcUrl);
    }

    public String getContractAddress() {
        return ongContract;
    }


    public String sendTransfer(Account sendAcct, String recvAddr, long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception {

        return sdk.nativevm().ong().sendTransfer(sendAcct,recvAddr,amount,payerAcct,gaslimit,gasprice);
    }

    public Transaction makeTransfer(String sendAddr, String recvAddr, long amount, String payer, long gaslimit, long gasprice) throws Exception {
        return sdk.nativevm().ong().makeTransfer(sendAddr,recvAddr,amount,payer,gaslimit,gasprice);
    }
    public Transaction makeTransfer(State[] states, String payer, long gaslimit, long gasprice) throws Exception {
        return sdk.nativevm().ong().makeTransfer(states,payer,gaslimit,gasprice);
    }

    public String sendApprove(Account sendAcct, String recvAddr, long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        return sdk.nativevm().ong().sendApprove(sendAcct,recvAddr,amount,payerAcct,gaslimit,gasprice);
    }
    public Transaction makeApprove(String sendAddr,String recvAddr,long amount,String payer,long gaslimit,long gasprice) throws Exception {
        return sdk.nativevm().ong().makeApprove(sendAddr,recvAddr,amount,payer,gaslimit,gasprice);
    }

    public String sendTransferFrom(Account sendAcct, String fromAddr, String toAddr, long amount, Account payerAcct, long gaslimit, long gasprice) throws Exception {
        return sdk.nativevm().ong().sendTransferFrom(sendAcct,fromAddr,toAddr,amount,payerAcct,gaslimit,gasprice);
    }

    public Transaction makeTransferFrom(String sendAddr, String fromAddr, String toAddr,long amount,String payer,long gaslimit,long gasprice) throws Exception {
        return sdk.nativevm().ong().makeTransferFrom(sendAddr,fromAddr,toAddr,amount,payer,gaslimit,gasprice);
    }

    public String queryName() throws Exception {
        return sdk.nativevm().ong().queryName();
    }

    /**
     * @return
     * @throws Exception
     */
    public String querySymbol() throws Exception {
        return sdk.nativevm().ong().querySymbol();
    }

    /**
     * @return
     * @throws Exception
     */
    public long queryDecimals() throws Exception {

        return sdk.nativevm().ong().queryDecimals();
    }

    public long queryTotalSupply() throws Exception {

        return sdk.nativevm().ong().queryTotalSupply();
    }

    public String unboundOng(String address) throws Exception {
        return sdk.nativevm().ong().unboundOng(address);
    }
    public long queryBalanceOf(String address) throws Exception {
        return sdk.nativevm().ong().queryBalanceOf(address);
    }

    public long queryAllowance(String fromAddr, String toAddr) throws Exception {
        return sdk.nativevm().ong().queryAllowance(fromAddr,toAddr);
    }

    public String ongxSetSyncAddr(Account[] accounts,byte[][] allPubkeys,int M,String address, Account payer, long gaslimit, long gasprice) throws Exception {
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(Address.decodeBase58(address));
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ongContract)),"setSyncAddr",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx, new Account[][]{{payer}});
        for(Account account : accounts){
            sdk.addMultiSign(tx, M,allPubkeys, account);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toHexString();
        }
        return null;
    }

    public String ongxSetSyncAddr(Account account,String address, Account payer, long gaslimit, long gasprice) throws Exception {
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(Address.decodeBase58(address));
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ongContract)),"setSyncAddr",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.signTx(tx, new Account[][]{{account}});
        if(!account.equals(payer)){
            sdk.addSign(tx, payer);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toHexString();
        }
        return null;
    }

    public String ongxInflation(Account account, Swap[] swaps, Account payer, long gaslimit, long gasprice) throws Exception {

        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(swaps.length);
        for (Swap swap : swaps) {
            struct.add(swap.address, swap.value);
        }
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ongContract)),"inflation",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);

        sdk.addSign(tx, account);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toHexString();
        }
        return null;
    }


    public String ongxSwap(Account account, Swap swap, Account payer, long gaslimit, long gasprice) throws Exception {
        List list = new ArrayList();
        Struct struct = new Struct();
        struct.add(swap.address, swap.value);
        list.add(struct);
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().buildNativeParams(new Address(Helper.hexToBytes(ongContract)),"swap",args,payer.getAddressU160().toBase58(),gaslimit, gasprice);
        sdk.addSign(tx, account);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toHexString();
        }
        return null;
    }

}
