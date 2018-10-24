package com.github.ontio.server;

import com.github.ontio.OntSdk;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ShadowChainServer {
    private OntSdk sdk;
    private String url;

    public static String NodeUrl1 = "http://127.0.0.1:20336";
    public static String NodeUrl2 = "http://polaris1.ont.io:20336";
    public static String[] NodeUrls = new String[]{NodeUrl1, NodeUrl2};
    public static Map<String, Integer> NodeUrlIndex = new HashMap(){};

    static {
        NodeUrlIndex.put(NodeUrl1,0);
    }

    public ShadowChainServer(String url) {
        this.url = url;
        this.sdk = OntSdk.getInstance();
        this.sdk.setRpc(url);
    }
    public void startServer(int height) {

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
    public void changeUrl(){
        int index = NodeUrlIndex.get(this.url);
        int i = (index+1) % NodeUrls.length;
        sdk.setRpc(NodeUrls[i]);
        this.url = NodeUrls[i];
    }

    public boolean verifyHeight(int height) throws IOException, InterruptedException {
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
