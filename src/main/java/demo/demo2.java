package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.server.ShadowChainServer;
import com.github.ontio.server.base.Common;

public class demo2 {
    public static Object lock = new Object();
    public static void main(String[] args) throws Exception {
        OntSdk sdk = getOntSdk();
        sdk.setRpc("http://139.219.128.220:20336");
        int h = 0;
        while (true){
            Common.monitor(sdk.getRpc(),h,"0000000000000000000000000000000000000007","ongSwag");
            h++;
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
