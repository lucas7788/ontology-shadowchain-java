package demo.test;

import java.util.ArrayList;
import java.util.List;

public class MyList {
    private static List<String> list = new ArrayList<String>();

    public static void add(String str) {
        list.add(str);
    }

    public static void remove(int i){
        list.remove(i);
    }
    public static String get(int i){
        return list.get(i);
    }

    public static int size() {
        return list.size();
    }
}
