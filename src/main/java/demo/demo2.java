package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.common.SmartCodeEvent;
import com.github.ontio.server.ShadowChainServer;
import com.github.ontio.server.base.Common;
import com.github.ontio.server.base.MonitorParam;
import com.github.ontio.server.base.MsgQueue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class demo2 {
    public static Object lock = new Object();
    public static void main(String[] args) throws Exception {
        OntSdk sdk = getOntSdk();
        sdk.setRpc("http://139.219.128.220:30336");
        int h = 7852;
        MonitorParam param = new MonitorParam("0200000000000000000000000000000000000000","ongxSwap");
        Object obj = Common.monitor(sdk.getRpc(),h,new MonitorParam[]{param});


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
