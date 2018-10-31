package com.github.ontio.server.base;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.governance.*;
import com.github.ontio.core.sidechaingovernance.SwapParam;
import com.github.ontio.io.Serializable;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.server.ShadowChainServer;
import com.github.ontio.shadowchain.smartcontract.ongx.Swap;
import com.github.ontio.shadowexception.ShadowErrorCode;
import com.github.ontio.shadowexception.ShadowException;

import java.util.concurrent.BlockingQueue;


public class ThreadHandle implements Runnable {
    private ShadowChainServer server;
    private final MsgQueue msgQueue;
    public ThreadHandle(ShadowChainServer shadowChainServer,final MsgQueue msgQueue){

        this.msgQueue = msgQueue;
        this.server = shadowChainServer;
    }

    @Override
    public void run() {
        while (true){
            for (String e : msgQueue.getDataSet()) {
                MsgInfo msgInfo = JSON.parseObject(e, MsgInfo.class);
                try {
                    sendTransaction(msgInfo);
                    Common.saveHandleSendTransaction(Common.HANDLE_SENDTRANSACTION,e,msgInfo.blockHeight);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    break;
                }
                msgQueue.removeData(e);
            }
        }
    }

    public void sendTransaction(MsgInfo msgInfo) throws Exception {
        if(msgInfo.chainType.equals("sideChain")){
            if(msgInfo.functionName.equals("ongxSwap")){
                SwapParam param= new SwapParam(msgInfo.sideChainId,Address.decodeBase58(msgInfo.address),msgInfo.amount);
                String txhash = server.getSdk().nativevm().sideChainGovernance().ongxSwap(server.getAdmin(),param,server.getAdmin(),
                        server.getConfig().gasLimit,server.getConfig().gasPrice);
                Object res = Common.waitResult(server.getSdk().getRpc(), txhash);
                if(!Common.verifyResult(res)){
                    System.out.println("failed transaction, txhash is : " + txhash);
                    throw new ShadowException(ShadowErrorCode.OtherError("ongSwap error"));
                }
            }else if(msgInfo.functionName.equals("commitDpos")){
                GovernanceView view = server.getSdk().nativevm().governance().getGovernanceView();
                String txhash = server.getShadowChain().getGovernance().inputGovernanceView(server.getAdmin(), view,server.getAdmin(),
                        server.getConfig().gasLimit,server.getConfig().gasPrice);
                Object res = Common.waitResult(server.getShadowChain().getSideChainRpcClient(), txhash);
                if(!Common.verifyResult(res)){
                    System.out.println("failed transaction, txhash is : " + txhash);
                    throw new ShadowException(ShadowErrorCode.OtherError("inputGovernanceView error"));
                }
                Configuration configuration = server.getSdk().nativevm().governance().getConfiguration();
                String txhash2 = server.getShadowChain().getGovernance().inputConfig(server.getAdmin(),configuration,server.getAdmin(),
                        server.getConfig().getGasLimit(),server.getConfig().gasPrice);
                Object res2 = Common.waitResult(server.getShadowChain().getSideChainRpcClient(), txhash2);
                if(!Common.verifyResult(res2)){
                    System.out.println("failed transaction, txhash is : " + txhash);
                    throw new ShadowException(ShadowErrorCode.OtherError("inputConfig error"));
                }
                GlobalParam param = server.getSdk().nativevm().governance().getGlobalParam();
                String txhash3 = server.getShadowChain().getGovernance().inputGlobalParam(server.getAdmin(),param,server.getAdmin(),
                        server.getConfig().getGasLimit(),server.getConfig().gasPrice);
                Object res3 = Common.waitResult(server.getShadowChain().getSideChainRpcClient(), txhash3);
                if(!Common.verifyResult(res3)){
                    System.out.println("failed transaction, txhash is : " + txhash);
                    throw new ShadowException(ShadowErrorCode.OtherError("inputGlobalParam error"));
                }
                SplitCurve curve = server.getSdk().nativevm().governance().getSplitCurve();
                String txhash4 = server.getShadowChain().getGovernance().inputSplitCurve(server.getAdmin(),curve,server.getAdmin(),
                        server.getConfig().getGasLimit(),server.getConfig().gasPrice);
                Object res4 = Common.waitResult(server.getShadowChain().getSideChainRpcClient(), txhash4);
                if(!Common.verifyResult(res4)){
                    System.out.println("failed transaction, txhash is : " + txhash);
                    throw new ShadowException(ShadowErrorCode.OtherError("inputSplitCurve error"));
                }
                InputPeerPoolMapParam param1 = server.getSdk().nativevm().governance().getInputPeerPoolMapParam("123456");
                String txhash5 = server.getShadowChain().getGovernance().inputPeerPoolMap(server.getAdmin(),param1,server.getAdmin(),
                        server.getConfig().getGasLimit(),server.getConfig().gasPrice);
                Object res5 = Common.waitResult(server.getShadowChain().getSideChainRpcClient(), txhash5);
                if(!Common.verifyResult(res5)){
                    System.out.println("failed transaction, txhash is : " + txhash);
                    throw new ShadowException(ShadowErrorCode.OtherError("inputPeerPoolMap error"));
                }
            }
        }else if(msgInfo.chainType.equals("mainChain")){
            if(msgInfo.functionName.equals("ongSwap")){
//                String sideChainId2 = server.getShadowChain().getGovernance().getSideChainId();
//                if(!msgInfo.sideChainId.equals(sideChainId2)){
//                    throw new ShadowException(ShadowErrorCode.OtherError("sidechainId error"));
//                }
//                SwapParam param= new SwapParam(msgInfo.sideChainId,Address.decodeBase58(msgInfo.address),msgInfo.amount);
                Swap swap = new Swap(Address.decodeBase58(msgInfo.address),msgInfo.amount);
                String txhash = server.getShadowChain().getOngX().ongSwap(server.getAdmin(),swap,server.getAdmin(),
                        server.getConfig().gasLimit,server.getConfig().gasPrice);
                Object res = Common.waitResult(server.getShadowChain().getSideChainRpcClient(), txhash);
                if(!Common.verifyResult(res)){
                    System.out.println("failed transaction, txhash is : " + txhash);
                    throw new ShadowException(ShadowErrorCode.OtherError("inputPeerPoolMap error"));
                }
            }else if(msgInfo.functionName.equals("commitDpos")){

            }
        }
    }
}
