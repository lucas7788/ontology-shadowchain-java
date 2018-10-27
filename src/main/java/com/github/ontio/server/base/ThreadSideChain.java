package com.github.ontio.server.base;

import com.github.ontio.common.*;
import com.github.ontio.core.sidechaingovernance.SwapParam;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.server.ShadowChainServer;
import com.github.ontio.shadowexception.ShadowException;

import java.io.IOException;
import java.util.List;

public class ThreadSideChain implements Runnable {

    private Object lock;
    private static String contractAddress = "0000000000000000000000000000000000000007";

    private ShadowChainServer shadowChainServer;

    public ThreadSideChain(ShadowChainServer shadowChainServer, Object obj){
        super();
        this.lock = obj;
        this.shadowChainServer = shadowChainServer;
    }
    @Override
    public void run() {

        int h = shadowChainServer.getConfig().getSideChainHeight();
        while (true){
            boolean ok = false;
            try {
                ok = Common.verifyHeight(shadowChainServer.getShadowChain().getSideChainRpcClient(),h);
                if(!ok){
                    continue;
                }
            } catch (IOException|InterruptedException e) {
                e.printStackTrace();
                break;
            } catch (ConnectorException e) {
                try {
                    Common.changeUrl(shadowChainServer,shadowChainServer.getShadowChain().getSideChainUrl());
                } catch (ShadowException e1) {
                    e1.printStackTrace();
                    break;
                }
            }
            List<SmartCodeEvent> eventList = null;
            try {
                eventList = Common.monitor(shadowChainServer.getShadowChain().getSideChainRpcClient(),
                        shadowChainServer.getConfig().sideChainHeight,contractAddress,"ongxSwap");
                handleSideChainEvent(shadowChainServer, eventList);
            } catch (ConnectorException e) {
                try {
                    Common.changeUrl(shadowChainServer,shadowChainServer.getShadowChain().getSideChainUrl());
                } catch (ShadowException e1) {
                    e1.printStackTrace();
                    break;
                }
            } catch (IOException|InterruptedException|ShadowException e) {
                e.printStackTrace();
            }
            h++;
        }
    }

    public void handleSideChainEvent(ShadowChainServer shadowChainServer, List<SmartCodeEvent> eventList) throws ConnectorException, ShadowException, IOException {

        synchronized (lock){
            for(SmartCodeEvent event : eventList){
                for(NotifyEventInfo info : event.Notify){
//            监听子链的ongxSwap事件
                    if(info.States != null && info.States.size() !=0){
                        if(info.States.get(0).equals("ongxSwap")){
                            String address = (String) info.States.get(1);
                            int amount = (int) info.States.get(2);
                            Common.saveNotify("sideChainNotify.csv",info.States);
                            String sideChainId = shadowChainServer.getShadowChain().getGovernance().getSideChainId();
                            MsgInfo msgInfo = new MsgInfo("mainChain",shadowChainServer.getMainChainUrl(),"ongxSwap",
                                    sideChainId,address,amount);
                            MsgQueue.addData(msgInfo);
//                      更新主链
//                        shadowChainServer.getSdk().setRpc(shadowChainServer.getMainChainUrl());
//                        SwapParam swapParam = new SwapParam("123456",Address.decodeBase58(address),amount);
//                        String txhash = null;
//                        try {
//                            txhash = shadowChainServer.getSdk().nativevm().sideChainGovernance().ongxSwap(shadowChainServer.getAdmin(),
//                                    swapParam,shadowChainServer.getAdmin(),shadowChainServer.getConfig().gasLimit,shadowChainServer.getConfig().gasPrice);;
//                            Common.waitResult(shadowChainServer.getSdk().getRpc(), txhash);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                        }
                    }
                }
            }
            lock.notify();
        }
    }
}
