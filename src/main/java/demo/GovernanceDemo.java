package demo;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.governance.*;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.network.rpc.RpcClient;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.shadowchain.smartcontract.governance.Governance;

import java.util.Base64;
import java.util.Map;

public class GovernanceDemo {
    public static void main(String[] args) throws Exception {
        String mainChainUrl = "http://139.219.128.220:20336";
        String sideChainUrl = "http://139.219.128.220:30336";
        OntSdk sdk = OntSdk.getInstance();
        sdk.openWalletFile("wallet.dat");
        sdk.setRpc(mainChainUrl);
//        sdk.setRpc(sideChainUrl);
        RpcClient rpcClient = new RpcClient(sideChainUrl);
        Governance governance = new Governance(sdk, rpcClient);
        String password = "111111";
        Account account = sdk.getWalletMgr().getAccount("AHX1wzvdw9Yipk7E9MuLY4GGX4Ym9tHeDe",password);
        Identity identity = sdk.getWalletMgr().getWallet().getIdentity("did:ont:Abrc5byDEZm1CnQb3XjAosEt34DD4w5Z1o");
        String sideChainContractAddr = "0000000000000000000000000000000000000008";


        //梦航
//        Account adminOntIdAcct = getAccount("cCQnie0Dd8aQPyY+9UBFw2x2cLn2RMogKqhM8OkyjJNrNTvlcVaYGRENfy2ErF7Q","passwordtest","ARiwjLzjzLKZy8V43vm6yUcRG9b56DnZtY","3e1zvaLjtVuPrQ1o7oJsQA==");
//        String adminPrivateKey =Helper.toHexString(adminOntIdAcct.serializePrivateKey());
        Identity adminIndentity = sdk.getWalletMgr().getWallet().getIdentity("did:ont:ARiwjLzjzLKZy8V43vm6yUcRG9b56DnZtY");
        //梦航
        Account account1 = getAccount("wR9S/JYwMDfCPWFGEy5DEvWfU14k9suZuL4+woGtfhZJf5+KyL9VJqMi/wGTOd1i","passwordtest","AZqk4i7Zhfhc1CRUtZYKrLw4YTSq4Y9khN","ZaIL8DxNaQ91fkMHAdiBjQ==");
        Account account2 = getAccount("PCj/a4zUgYnOBNZUVEaXBK61Sq4due8w2RUzrumO3Bm0hZ/3v4mlDiXYYvmmBZUk","passwordtest","ARpjnrnHEjXhg4aw7vY6xsY6CfQ1XEWzWC","wlz1h439j0GwsWhGBByMxg==");
        Account account3 = getAccount("4U6qYhRUxGYTcvDvBKKCu2C1xUyd0A+pHXsK1YVY1Hbxd8TcbyvmfOcqx7N+f+BH","passwordtest","AQs2BmzzFVk7pQPfTQQi9CTEz43ejSyBnt","AFDFoZAlLGJdB4yVQqYVhw==");
        Account account4 = getAccount("i6n+FTACzRF5y0oeo6Wm3Zbv68bfjmyRyNfKB5IArK76RCG8b/JgRqnHgMtHixFx","passwordtest","AKBSRLbFNvUrWEGtKxNTpe2ZdkepQjYKfM","FkTZ6czRPAqHnSpEqVEWwA==");
        Account account5 = getAccount("IoEbJXMPlxNLrAsDYKGD4I6oFYgJl1j603c8oHQl+82yET+ibKgJdZjgdw39pr2K","passwordtest","AduX7odaWGipkdvzBwyaTgsumRbRzhhiwe","lc7ofKCBkNUmjTLrZYmStA==");
        Account account6 = getAccount("6hynBJVTAhmMJt9bIYSDoz+GL5EFaUGhn3Pd6HsF+RQ1tFyZoFRhT+JNMGAb+B6a","passwordtest","ANFfWhk3A5iFXQrVBHKrerjDDapYmLo5Bi","DTmbW9wzGA8pi4Dcj3/Cpg==");
        Account account7 = getAccount("EyXxszzKh09jszQXMIFJTmbujnojOzYzPU4cC0wOpuegDgVcRFllATQ81zD0Rp8s","passwordtest","AK3YRcRvKrASQ6nTfW48Z4iMZ2sDTDRiMC","jbwUF7JxgsiJq5QAy5dfug==");
        Address multiAddress = Address.addressFromMultiPubKeys(5,account1.serializePublicKey(),account2.serializePublicKey(),account3.serializePublicKey(),account4.serializePublicKey(),account5.serializePublicKey(),account6.serializePublicKey(),account7.serializePublicKey());
        Account[] accounts = new Account[]{account1,account2,account3,account4,account5,account6,account7};
        byte[][] pks = new byte[accounts.length][];
        for(int i=0;i<pks.length;i++){
            pks[i] = accounts[i].serializePublicKey();
        }
        if(false){
            System.out.println(governance.getSideChainId());
            return;
        }
        if(false){
            sdk.setRpc(sideChainUrl);
            String txhash = sdk.nativevm().governance().commitDpos(multiAddress,5,new Account[]{account1,account2,account3,account4,account5},new byte[][]{account6.serializePublicKey(),account7.serializePublicKey()},
                    account1,sdk.DEFAULT_GAS_LIMIT,0);

            Thread.sleep(6000);
            System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
            return;
        }
        if(false){
//            success
            sdk.setRpc(mainChainUrl);
            GovernanceView view = sdk.nativevm().governance().getGovernanceView();
            System.out.println(JSON.toJSONString(view));
            governance.setRpcUrl(sideChainUrl);
            GovernanceView view1 = governance.getGovernanceView();
            System.out.println(JSON.toJSONString(view1));

//            String txhash = governance.inputGovernanceView(account,view,account,20000,0);
//            System.out.println(txhash);
//            Thread.sleep(6000);
//            System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
            return;
        }

        if(false){
//            success
            sdk.setRpc(mainChainUrl);
            Configuration configuration = sdk.nativevm().governance().getConfiguration();
            System.out.println(JSON.toJSONString(configuration));
            configuration.MaxBlockChangeView = 20000;
            governance.setRpcUrl(sideChainUrl);
            Configuration configuration1 = governance.getConfiguration();
            System.out.println(JSON.toJSONString(configuration1));
            String txhash = governance.inputConfig(account,configuration,account,20000,0);
            Thread.sleep(6000);
            System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
            return;
        }

        if(false){
//            success
            sdk.setRpc(mainChainUrl);
            GlobalParam param = sdk.nativevm().governance().getGlobalParam();
            System.out.println(JSON.toJSONString(param));

            governance.setRpcUrl(sideChainUrl);
            GlobalParam param1 = governance.getGlobalParam();
            System.out.println(JSON.toJSONString(param1));
//
//            String txhash = governance.inputGlobalParam(account,param,account,20000,0);
//            Thread.sleep(6000);
//            System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));

            return;
        }

        if(true){
            sdk.setRpc(mainChainUrl);
            InputPeerPoolMapParam peerPoolMap = sdk.nativevm().governance().getInputPeerPoolMapParam("123456");
            System.out.println(JSON.toJSONString(peerPoolMap.peerPoolMap));
            PeerPoolItem item = new PeerPoolItem();
            peerPoolMap.peerPoolMap.get("0281d198c0dd3737a9c39191bc2d1af7d65a44261a8a64d6ef74d63f27cfb5ed92").address = account.getAddressU160();
            governance.setRpcUrl(sideChainUrl);

            String txhash = governance.inputPeerPoolMap(account,peerPoolMap,account,20000,0);
            Thread.sleep(6000);
            System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
            Map param = governance.getPeerPoolMap();
            return;
        }

        if(false){
//            success
            sdk.setRpc(mainChainUrl);
            SplitCurve curve = sdk.nativevm().governance().getSplitCurve();
            System.out.println(JSON.toJSONString(curve));

            governance.setRpcUrl(sideChainUrl);

            curve.Yi[0] = 1;
            String txhash = governance.inputSplitCurve(account,curve,account,20000,0);
            Thread.sleep(6000);
            System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
            SplitCurve curve1 = governance.getSplitCurve();
            System.out.println(JSON.toJSONString(curve1));
            return;
        }

    }

    public static Account getAccount(String enpri,String password,String address,String salt) throws Exception {
        String privateKey = Account.getGcmDecodedPrivateKey(enpri,password,address,Base64.getDecoder().decode(salt),16384,SignatureScheme.SHA256WITHECDSA);
        Account account = new Account(Helper.hexToBytes(privateKey),SignatureScheme.SHA256WITHECDSA);
//        System.out.println(Helper.toHexString(account.serializePublicKey()));
        return account;
    }
}
