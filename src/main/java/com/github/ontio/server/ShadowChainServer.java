package com.github.ontio.server;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Config;
import com.github.ontio.common.ShadowErrorCode;
import com.github.ontio.common.ShadowException;
import com.github.ontio.network.exception.ConnectorException;
import java.io.*;

public class ShadowChainServer {
    private OntSdk sdk;
    private Config config;
    private String nodeUrl;

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
        } catch (FileNotFoundException e) {
            System.out.println("there is no the file");
            throw new ShadowException(ShadowErrorCode.OtherError("the path of config.json is wrong:" + path));
        }catch (IOException e){
            System.out.println(e.getMessage());
            throw new ShadowException(ShadowErrorCode.OtherError("read file error:" + path));
        }

    }
    public void initServer() throws ShadowException {
        if(config == null){
            throw new ShadowException(ShadowErrorCode.OtherError("please first read config"));
        }
        if(config.mainChainUrl.size() == 0){
            throw new ShadowException(ShadowErrorCode.OtherError("there is not the main chain url config"));
        }
        nodeUrl = config.mainChainUrl.get(0);
        sdk.setRpc(nodeUrl);

    }
    public void startServer(int height) throws ShadowException {

        while (true) {
            try {
                boolean ok = verifyHeight(height);
                if (!ok){
                    continue;
                }
                Object obj = sdk.getConnect().getSmartCodeEvent(height);
                if(obj != null){
                    System.out.println("Notify:" + obj);
                    Thread.sleep(3000);
                }
                height++;
            } catch (ConnectorException e) {
                System.out.println("Abnormal network connection");
                changeUrl();
            } catch (IOException|InterruptedException e) {
                System.out.println(e.getMessage());
                saveData(height);
                break;
            }
        }
    }

    public void saveData(int height) {
        File file = new File("result.txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("height: " + height);
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        try{
            FileWriter fw=new FileWriter(file);
            BufferedWriter bw=new BufferedWriter(fw);
            bw.write(height + "\n");
            bw.close();
            fw.close();
        }catch (FileNotFoundException e){
            saveData(height);
        }catch (IOException e){
            System.out.println("height: " + height);
            System.out.println(e.getMessage());
        }

    }

    public void waiteMoment(int currentHeight) throws InterruptedException {
        System.out.println("current block height is " + currentHeight);
        System.out.println("please wait...");
        Thread.sleep(6000);
    }
    public void changeUrl() throws ShadowException {
        if(!config.mainChainUrl.contains(nodeUrl)){
            throw new ShadowException(ShadowErrorCode.OtherError("the node url is wrong" + nodeUrl));
        }
        int i = config.mainChainUrl.indexOf(nodeUrl);
        int nextI = i+1%config.mainChainUrl.size();
        nodeUrl = config.mainChainUrl.get(nextI);
        sdk.setRpc(nodeUrl);
    }

    public boolean verifyHeight(int height) throws IOException, InterruptedException, ShadowException {
        try{
            int currentHeight = sdk.getConnect().getBlockHeight();
            if (currentHeight < height){
                waiteMoment(currentHeight);
                return false;
            }
        }catch (ConnectorException e){
            changeUrl();
        }
        return true;
    }
}
