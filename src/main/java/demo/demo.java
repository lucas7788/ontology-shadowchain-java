package demo;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.core.block.Block;
import com.github.ontio.io.Serializable;
import com.github.ontio.network.websocket.MsgQueue;
import com.github.ontio.network.websocket.Result;

import java.util.HashMap;
import java.util.Map;

public class demo {

    public static Object lock = new Object();
    public static void main(String[] args) {
        // 1. 启动监听server，监听主链
//         2.  更新侧链数据
        try {

            OntSdk sdk = getOntSdk();
            sdk.getWebSocket().startWebsocketThread(false);
            Thread thread = new Thread(
                    new Runnable() {
                        public void run() {
                            waitResult(lock);
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
        } catch (Exception e) {
            e.printStackTrace();
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

    public static OntSdk getOntSdk() throws Exception {
        String ip = "http://127.0.0.1";
//        String ip = "http://139.219.129.55";
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setWesocket(wsUrl, lock);
        wm.setDefaultConnect(wm.getWebSocket());
        wm.openWalletFile("OntAssetDemo.json");
        return wm;
    }
}
