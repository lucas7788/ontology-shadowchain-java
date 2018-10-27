package com.github.ontio.server.base;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.sidechaingovernance.SwapParam;
import com.github.ontio.io.Serializable;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.server.ShadowChainServer;
import com.github.ontio.shadowchain.smartcontract.ongx.Swap;
import com.github.ontio.shadowexception.ShadowErrorCode;
import com.github.ontio.shadowexception.ShadowException;


public class ThreadHandle implements Runnable {
    private Object lock;
    private ShadowChainServer server;

    public ThreadHandle(ShadowChainServer shadowChainServer,Object lock){

        this.lock = lock;
        this.server = shadowChainServer;
    }

    @Override
    public void run() {
        while (true){
            synchronized (lock){
                try {
                    lock.wait();
                    for (String e : MsgQueue.getDataSet()) {
                        MsgInfo msgInfo = JSON.parseObject(e, MsgInfo.class);
                        sendTransaction(msgInfo);
                        MsgQueue.removeData(e);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendTransaction(MsgInfo msgInfo) throws Exception {
        if(msgInfo.chainType.equals("sideChain")){
            if(msgInfo.functionName.equals("ongSwap")){
                SwapParam param= new SwapParam(msgInfo.sideChainId,Address.decodeBase58(msgInfo.address),msgInfo.amount);
                String txhash = server.getSdk().nativevm().sideChainGovernance().ongSwap(server.getAdmin(),param,server.getAdmin(),
                        server.getConfig().gasLimit,server.getConfig().gasPrice);
                Common.waitResult(server.getShadowChain().getSideChainRpcClient(), txhash);
            }
        }else if(msgInfo.chainType.equals("mainChain")){
            if(msgInfo.functionName.equals("ongxSwap")){
                String sideChainId2 = server.getShadowChain().getGovernance().getSideChainId();
                if(msgInfo.sideChainId != sideChainId2){
                    throw new ShadowException(ShadowErrorCode.OtherError("sidechainId error"));
                }
                Swap swap = new Swap(Address.decodeBase58(msgInfo.address), msgInfo.amount);
                String txhash = server.getShadowChain().getOngX().ongSwap(server.getAdmin(),swap,
                        server.getAdmin(),server.getConfig().gasLimit,server.getConfig().gasPrice);
                Common.waitResult(server.getShadowChain().getSideChainRpcClient(), txhash);
            }
        }
    }
}
