package demo.testcsv;

import java.io.File;

public class TestCsv {
    public static void main(String[] args){
        File file = new File("hah.txt");
        System.out.println(file.exists());
    }
}