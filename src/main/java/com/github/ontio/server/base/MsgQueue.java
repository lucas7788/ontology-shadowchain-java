package com.github.ontio.server.base;

import com.alibaba.fastjson.JSON;

import java.util.HashSet;
import java.util.Set;

public class MsgQueue {
    private static Set<String> dataSet = new HashSet<>();
    public static void addData(MsgInfo info){
        dataSet.add(JSON.toJSONString(info));
    }

    public static Set<String> getDataSet() {
        Set<String> temp = new HashSet<String>();
        temp.addAll(dataSet);
        return temp;
    }

    public static void removeData(String data){
        dataSet.remove(data);
    }
    public static  void clear(){
        dataSet.clear();
    }
    public static int size() {
        return dataSet.size();
    }
}
