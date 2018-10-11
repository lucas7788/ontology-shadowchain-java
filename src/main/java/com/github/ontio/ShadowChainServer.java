package com.github.ontio;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.Helper;
import com.github.ontio.core.block.Block;
import com.github.ontio.io.Serializable;
import com.github.ontio.network.websocket.MsgQueue;
import com.github.ontio.network.websocket.Result;
import com.github.ontio.sdk.exception.SDKException;

import java.util.HashMap;
import java.util.Map;

public class ShadowChainServer {
    public OntSdk sdk;
    public static Object thelock;

    public ShadowChainServer(OntSdk sdk, Object lock){
        this.sdk = sdk;
        thelock = lock;
    }
    public void startServer() throws SDKException, InterruptedException {
        this.sdk.getWebSocket().startWebsocketThread(false);
        Thread thread = new Thread(
                new Runnable() {
                    public void run() {
                        waitResult(thelock);
                    }
                });
        thread.start();
        Thread.sleep(5000);
        for (int i = 0; i>= 0; i++) {
            if(true){
                Map map = new HashMap();
                if(i >0) {
                    map.put("SubscribeEvent", true);
                    map.put("SubscribeRawBlock", false);
                }else{
                    map.put("SubscribeJsonBlock", false);
                    map.put("SubscribeRawBlock", true);
                }
                sdk.getWebSocket().setReqId(i);
                sdk.getWebSocket().sendSubscribe(map);
            }
            Thread.sleep(6000);
        }
    }

    public static void waitResult(Object lock) {
        try {
            synchronized (lock) {
                while (true) {
                    lock.wait();
                    for (String e : MsgQueue.getResultSet()) {
                        System.out.println("RECV: " + e);
                        Result rt = JSON.parseObject(e, Result.class);
                        MsgQueue.removeResult(e);
                        if (rt.Action.equals("getblockbyheight")) {
                            Block bb = Serializable.from(Helper.hexToBytes((String) rt.Result), Block.class);
                            //System.out.println(bb.json());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
