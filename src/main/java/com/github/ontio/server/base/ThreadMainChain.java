package com.github.ontio.server.base;

import com.github.ontio.common.*;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.shadowchain.smartcontract.ongx.Swap;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.server.ShadowChainServer;
import com.github.ontio.shadowexception.ShadowErrorCode;
import com.github.ontio.shadowexception.ShadowException;

import java.io.IOException;
import java.util.List;

public class ThreadMainChain implements Runnable {
    private Object lock;
    private ShadowChainServer shadowChainServer;
    private static String contractAddress = "0000000000000000000000000000000000000007";

    public ThreadMainChain(ShadowChainServer shadowChainServer, Object obj){
        super();
        this.lock = obj;
        this.shadowChainServer = shadowChainServer;
    }

    @Override
    public void run() {
        int h = shadowChainServer.getConfig().getMainChainHeight();
        while (true){
            boolean ok = false;
            try {
                try {
                    ok = Common.verifyHeight(shadowChainServer.getSdk().getRpc(),h);
                    if (!ok){
                        continue;
                    }
                } catch (ConnectorException e){
                    try {
                        Common.changeUrl(shadowChainServer,shadowChainServer.getMainChainUrl());
                    } catch (ShadowException e1) {
                        e1.printStackTrace();
                        break;
                    }
                }catch (SDKException e){
                    e.printStackTrace();
                    break;
                }
            } catch (IOException|InterruptedException e) {
                e.printStackTrace();
                break;
            }

            try {
                List<SmartCodeEvent> event = Common.monitor(shadowChainServer.getSdk().getRpc(), h,contractAddress,"ongSwap");
                if(event == null || event.size()==0) {
                    continue;
                }
                try {
                    handleMainChainEvent(shadowChainServer,event);
                    h++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException|InterruptedException|SDKException e) {
                e.printStackTrace();
            } catch (ConnectorException e) {
                try {
                    Common.changeUrl(shadowChainServer,shadowChainServer.getMainChainUrl());
                } catch (ShadowException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    public void handleMainChainEvent(ShadowChainServer server,List<SmartCodeEvent> eventList) throws Exception {
        synchronized (lock){
            for(SmartCodeEvent event: eventList){
                for(NotifyEventInfo info : event.Notify){
//            监听到ongswap事件
                    if(info.States.get(0).equals("ongSwap")){
                        String sideChainId = (String) info.States.get(1);
                        String address = (String) info.States.get(2);
                        int amount = (int) info.States.get(3);
                        MsgInfo info1 = new MsgInfo("sideChain",server.getShadowChain().getSideChainUrl(),"ongSwap",
                                sideChainId,address,amount);
                        MsgQueue.addData(info1);
                    }
                }
            }
            lock.notify();
        }


//                      更新子链
//                        shadowChainServer.getShadowChain().getOngX().setRpcUrl(shadowChainServer.getShadowChain().getSideChainUrl());
//                        String sideChainId2 = shadowChainServer.getShadowChain().getGovernance().getSideChainId();
//                        if(sideChainId != sideChainId2){
//                            throw new ShadowException(ShadowErrorCode.OtherError("sidechainId error"));
//                        }
//                        Swap swap = new Swap(Address.decodeBase58(address), amount);
//                        String txhash = shadowChainServer.getShadowChain().getOngX().ongSwap(shadowChainServer.getAdmin(),swap,
//                                shadowChainServer.getAdmin(),shadowChainServer.getConfig().gasLimit,shadowChainServer.getConfig().gasPrice);
//                        Common.waitResult(shadowChainServer.getShadowChain().getSideChainRpcClient(), txhash);
    }
}
