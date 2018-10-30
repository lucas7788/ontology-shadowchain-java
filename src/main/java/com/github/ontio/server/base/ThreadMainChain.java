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
import java.util.concurrent.BlockingQueue;

public class ThreadMainChain implements Runnable {
    private final BlockingQueue<MsgQueue> queueMainChain;
    private ShadowChainServer shadowChainServer;

    public ThreadMainChain(ShadowChainServer shadowChainServer, final BlockingQueue<MsgQueue> queue){
        super();
        this.queueMainChain = queue;
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
                MonitorParam param1 = new MonitorParam("0700000000000000000000000000000000000000","ongSwap");
                MonitorParam param2 = new MonitorParam("0700000000000000000000000000000000000000","commitDpos");
                List<SmartCodeEvent> event = Common.monitor(shadowChainServer.getSdk().getRpc(), h,new MonitorParam[]{param1, param2});
                try {
                    if(event!=null && event.size()!=0){
                        handleMainChainEvent(shadowChainServer,event, h);
                    }
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


    public void handleMainChainEvent(ShadowChainServer server,List<SmartCodeEvent> eventList, int h) throws Exception {
        MsgQueue msgQueue = new MsgQueue();
        for(SmartCodeEvent event: eventList){
            for(NotifyEventInfo info : event.Notify){
//            监听到ongswap事件
                if(info.States.get(0).equals("ongSwap")){
                    String sideChainId = (String) info.States.get(1);
                    String address = (String) info.States.get(2);
                    int amount = (int) info.States.get(3);
                    MsgInfo info1 = new MsgInfo("mainChain",server.getShadowChain().getSideChainUrl(),"ongSwap",
                            sideChainId,address,amount,h);
                    msgQueue.addData(info1);
                }else if(info.States.get(0).equals("commitDpos")){
                    MsgInfo info1 = new MsgInfo("mainChain",server.getShadowChain().getSideChainUrl(),"commitDpos",
                            "","",0,h);
                    msgQueue.addData(info1);
                }
            }
        }
        queueMainChain.add(msgQueue);


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
