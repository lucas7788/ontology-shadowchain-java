package com.github.ontio.server;
import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.server.base.*;
import com.github.ontio.server.config.Config;
import com.github.ontio.shadowchain.ShadowChain;
import com.github.ontio.shadowexception.ShadowErrorCode;
import com.github.ontio.shadowexception.ShadowException;

import java.io.*;
import java.util.concurrent.*;

public class ShadowChainServer {
    private OntSdk sdk;
    private Config config;
    private String mainChainUrl;
    private Account admin;
    private ShadowChain shadowChain;
    private static final ThreadPoolExecutor THREAD_POOL = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private static final ScheduledExecutorService listerMainChainService = Executors.newSingleThreadScheduledExecutor();
    private static final ScheduledExecutorService listerSideChainService = Executors.newSingleThreadScheduledExecutor();
    public ShadowChainServer() {
        this.sdk = OntSdk.getInstance();
    }

    public void startServer() throws InterruptedException {

        BlockingQueue<MsgQueue> queueMainChain = new ArrayBlockingQueue<MsgQueue>(100);
//        监听主链
        listerMainChainService.execute(new ThreadMainChain(this, queueMainChain));

        BlockingQueue<MsgQueue> queueSideChain = new ArrayBlockingQueue<MsgQueue>(100);
//        监听子链
        listerSideChainService.execute(new ThreadSideChain(this,queueSideChain));

//        处理监听到的消息
        while (true){
            final MsgQueue msgMainChain = queueMainChain.take();
            if(msgMainChain !=null && msgMainChain.getDataSet().size() !=0){
                THREAD_POOL.execute(new ThreadHandle(this, msgMainChain));
            }
            final MsgQueue msgSideChain = queueSideChain.take();
            System.out.println(msgSideChain.size());
            if(msgSideChain!=null && msgSideChain.size() !=0){
                THREAD_POOL.execute(new ThreadHandle(this, msgSideChain));
            }
        }
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
