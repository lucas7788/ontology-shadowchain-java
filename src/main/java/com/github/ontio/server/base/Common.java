package com.github.ontio.server.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.common.*;
import com.github.ontio.network.connect.IConnector;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.network.rpc.RpcClient;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.manager.ConnectMgr;
import com.github.ontio.server.ShadowChainServer;
import com.github.ontio.shadowexception.ShadowErrorCode;
import com.github.ontio.shadowexception.ShadowException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class Common {
    public static String CHAIN_NOTIFY = "-chainNotify.csv";
    public static String HANDLE_SENDTRANSACTION = "-handleSendTransaction.csv";

    public static boolean verifyHeight(ConnectMgr rpcClient, int height) throws IOException, InterruptedException, ConnectorException {
        int currentHeight = rpcClient.getBlockHeight();
        if (currentHeight < height){
            waiteMoment(currentHeight);
            return false;
        }
        return true;
    }

    public static void waiteMoment(int currentHeight) throws InterruptedException {
        System.out.println("current block height is " + currentHeight);
        System.out.println("please wait...");
        Thread.sleep(6000);
    }

    public static void changeUrl(ShadowChainServer shadowChainServer, String nodeUrl) throws ShadowException {
        if(shadowChainServer.getConfig().mainChainUrl.contains(nodeUrl)){
            int i = shadowChainServer.getConfig().mainChainUrl.indexOf(nodeUrl);
            int nextI = (i+1)%shadowChainServer.getConfig().mainChainUrl.size();
            shadowChainServer.updateMainChainUrl(shadowChainServer.getConfig().mainChainUrl.get(nextI));

        }else if(shadowChainServer.getConfig().shadowChainUrl.contains(nodeUrl)){
            int i = shadowChainServer.getConfig().shadowChainUrl.indexOf(nodeUrl);
            int nextI = (i+1)%shadowChainServer.getConfig().mainChainUrl.size();
            shadowChainServer.getShadowChain().updateSideChainUrl(shadowChainServer.getConfig().shadowChainUrl.get(nextI));
        }else {
            throw new ShadowException(ShadowErrorCode.OtherError("the node url is wrong" + nodeUrl));
        }

    }

    public static List<SmartCodeEvent> monitor(ConnectMgr rpcClient, int height, MonitorParam[] params) throws ConnectorException, IOException, InterruptedException {
        Object event = rpcClient.getSmartCodeEvent(height);
        if(event != null){
            List<SmartCodeEvent> eventList = new ArrayList<>();
            for (Object obj: (JSONArray)event){
                SmartCodeEvent smartCodeEvent = JSONObject.toJavaObject((JSONObject)obj,SmartCodeEvent.class);
                if(smartCodeEvent.getState() !=1||smartCodeEvent.getNotify() == null|| smartCodeEvent.getNotify().size() == 0){
                    continue;
                }
                for(NotifyEventInfo info : smartCodeEvent.getNotify()){
                    for(MonitorParam param:params){
                        if(info.ContractAddress.equals(param.contractAddress) && info.getStates().get(0).equals(param.functionName)){
                            eventList.add(smartCodeEvent);
                        }
                    }
                }
            }
            if(eventList.size() !=0){
                System.out.println("Notify:" + ((JSONArray)event).toJSONString());
                //                        保存日志
                Common.saveNotify2(CHAIN_NOTIFY,((JSONArray)event).toJSONString(), height);
            }
            return eventList;
        }
        return null;
    }

    public static Object waitResult(ConnectMgr rpcClient, String hash) throws Exception {
        Object objEvent = null;
        Object objTxState = null;
        int notInpool = 0;
        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(3000);
                objEvent = rpcClient.getSmartCodeEvent(hash);
                if (objEvent == null || objEvent.equals("")) {
                    Thread.sleep(1000);
                    objTxState = rpcClient.getMemPoolTxState(hash);
                    continue;
                }
                if (((Map) objEvent).get("Notify") != null) {
                    return objEvent;
                }
            } catch (Exception e) {
                if (e.getMessage().contains("UNKNOWN TRANSACTION") && e.getMessage().contains("getmempooltxstate")) {
                    notInpool++;
                    if ((objEvent.equals("") || objEvent == null) && notInpool >1){
                        throw new SDKException(e.getMessage());
                    }
                } else {
                    continue;
                }
            }
        }
        throw new SDKException(ErrorCode.OtherError("time out"));
    }
    public static boolean verifyResult(Object event){
        if((int)((Map)event).get("State") == 1){
            return true;
        } else {
            return false;
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
    public static void saveHandleSendTransaction(String fileName, String msgInfo, int height){
        Calendar cal = Calendar.getInstance();
        java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(cal.getTime());
        fileName = currentDate + fileName;
        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            File finalCSVFile = new File(fileName);
            if(!finalCSVFile.exists()){
                out = new FileOutputStream(finalCSVFile, true);
                osw = new OutputStreamWriter(out, "UTF-8");
                // 手动加上BOM标识
                osw.write(new String(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF }));
                bw = new BufferedWriter(osw);
                /**
                 * 往CSV中写新数据
                 */
                String title = "";
                title = "height,msgInfo";
                bw.append(title).append("\r");
            }else {
                out = new FileOutputStream(finalCSVFile, true);
                osw = new OutputStreamWriter(out, "UTF-8");
                bw = new BufferedWriter(osw);
            }
            bw.append(Integer.toString(height));
            bw.append(",");
            bw.append(msgInfo);
            bw.append("\r");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (bw != null) {
                try {
                    bw.close();
                    bw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                    osw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    public static void saveNotify2(String fileName, String notify, int height){
        Calendar cal = Calendar.getInstance();
        java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(cal.getTime());
        fileName = currentDate + fileName;
        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            File finalCSVFile = new File(fileName);
            if(!finalCSVFile.exists()){
                out = new FileOutputStream(finalCSVFile, true);
                osw = new OutputStreamWriter(out, "UTF-8");
                // 手动加上BOM标识
                osw.write(new String(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF }));
                bw = new BufferedWriter(osw);
                /**
                 * 往CSV中写新数据
                 */
                String title = "";
                title = "height,notify";
                bw.append(title).append("\r");
            }else {
                out = new FileOutputStream(finalCSVFile, true);
                osw = new OutputStreamWriter(out, "UTF-8");
                bw = new BufferedWriter(osw);
            }
            bw.append(Integer.toString(height));
            bw.append(",");
            bw.append(notify);
            bw.append("\r");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (bw != null) {
                try {
                    bw.close();
                    bw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                    osw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void saveNotify(String fileName, List<Object> list){
        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            File finalCSVFile = new File(fileName);
            out = new FileOutputStream(finalCSVFile);
            osw = new OutputStreamWriter(out, "UTF-8");
            // 手动加上BOM标识
            osw.write(new String(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF }));
            bw = new BufferedWriter(osw);
            /**
             * 往CSV中写新数据
             */
            String title = "";
            title = "functionName,sideChianId,address,amount";
            bw.append(title).append("\r");

            if (list != null && !list.isEmpty()) {
                for (int i=0;i<list.size();i++) {
                    if(i == list.size()-1){
                        bw.append(list.get(i)+"");
                    }else {
                        bw.append(list.get(i) + ",");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (bw != null) {
                try {
                    bw.close();
                    bw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                    osw = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
