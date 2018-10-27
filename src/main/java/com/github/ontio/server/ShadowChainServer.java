package com.github.ontio.server;
import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.server.base.ThreadHandle;
import com.github.ontio.server.base.ThreadMainChain;
import com.github.ontio.server.base.ThreadSideChain;
import com.github.ontio.server.config.Config;
import com.github.ontio.shadowchain.ShadowChain;
import com.github.ontio.shadowexception.ShadowErrorCode;
import com.github.ontio.shadowexception.ShadowException;

import java.io.*;

public class ShadowChainServer {
    private OntSdk sdk;
    private Config config;
    private String mainChainUrl;
    private Account admin;
    private ShadowChain shadowChain;

    public ShadowChainServer() {

        this.sdk = OntSdk.getInstance();
    }

    public void readConfig(String path) throws ShadowException {
        File file = new File(path);
        if(!file.exists()){
            throw new ShadowException(ShadowErrorCode.OtherError("the path of config.json is wrong:" + path));
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String text = new String(bytes);
            config = JSON.parseObject(text, Config.class);
            if(config.mainChainUrl.size() == 0 || config.shadowChainUrl.size() == 0){
                throw new ShadowException(ShadowErrorCode.OtherError("there is no the main chain url or side chain url"));
            }
            mainChainUrl = config.mainChainUrl.get(0);
            sdk.openWalletFile(config.wallet);
            sdk.setRpc(mainChainUrl);
            shadowChain = new ShadowChain(sdk, config.shadowChainUrl.get(0));
            admin = sdk.getWalletMgr().getAccount(config.admin.get("address"),config.admin.get("password"));

        } catch (FileNotFoundException e) {
            System.out.println("there is no the file");
            throw new ShadowException(ShadowErrorCode.OtherError("the path of config.json is wrong:" + path));
        }catch (IOException e){
            System.out.println(e.getMessage());
            throw new ShadowException(ShadowErrorCode.OtherError("read file error:" + path));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void startServer() {
        Object lock = new Object();
//        监听主链
        ThreadMainChain mainChain = new ThreadMainChain(this, lock);
        new Thread(mainChain).start();

//        监听子链
        ThreadSideChain sideChain = new ThreadSideChain(this,lock);
        new Thread(sideChain).start();

        ThreadHandle handle = new ThreadHandle(this,lock);
        new Thread(handle).start();
    }


    public Config getConfig(){
        return config;
    }
    public String getMainChainUrl() {
        return mainChainUrl;
    }
    public void updateMainChainUrl(String nodeUrl){
        this.mainChainUrl = nodeUrl;
    }
    public Account getAdmin() {
        return admin;
    }
    public OntSdk getSdk() {
        return sdk;
    }
    public ShadowChain getShadowChain() {
        return shadowChain;
    }
}
