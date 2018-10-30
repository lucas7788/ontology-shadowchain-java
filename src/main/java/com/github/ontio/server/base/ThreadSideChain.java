package com.github.ontio.server.base;

import com.github.ontio.common.*;
import com.github.ontio.core.sidechaingovernance.SwapParam;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.server.ShadowChainServer;
import com.github.ontio.shadowexception.ShadowException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ThreadSideChain implements Runnable {

    private BlockingQueue<MsgQueue> queueSideChain;
    private static String contractAddress = "0200000000000000000000000000000000000000";

    private ShadowChainServer shadowChainServer;

    public ThreadSideChain(ShadowChainServer shadowChainServer, BlockingQueue<MsgQueue> queueSideChain){
        super();
        this.queueSideChain = queueSideChain;
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
                MonitorParam param = new MonitorParam(contractAddress,"ongxSwap");
                eventList = Common.monitor(shadowChainServer.getShadowChain().getSideChainRpcClient(),
                        h,new MonitorParam[]{param});
                if(eventList!=null && eventList.size() !=0){
                    handleSideChainEvent(shadowChainServer, eventList, h);
                }
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

    public void handleSideChainEvent(ShadowChainServer shadowChainServer, List<SmartCodeEvent> eventList, int blockHeight) throws ConnectorException, ShadowException, IOException {
        MsgQueue msgQueue = new MsgQueue();
        for(SmartCodeEvent event : eventList){
            for(NotifyEventInfo info : event.Notify){
//            监听子链的ongxSwap事件
                if(info.States != null && info.States.size() !=0){
                    if(info.States.get(0).equals("ongxSwap")){
                        String address = (String) info.States.get(1);
                        int amount = (int) info.States.get(2);
                        String sideChainId = shadowChainServer.getShadowChain().getGovernance().getSideChainId();
                        MsgInfo msgInfo = new MsgInfo("mainChain",shadowChainServer.getMainChainUrl(),"ongxSwap",
                                sideChainId,address,amount,blockHeight);
                        msgQueue.addData(msgInfo);
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
        queueSideChain.add(msgQueue);
    }
}
