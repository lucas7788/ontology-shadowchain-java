package demo.testexector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolTest {
    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();
        ExecutorService threadPool = Executors.newCachedThreadPool();//线程池里面的线程数会动态变化，并可在线程线被移除前重用
//        threadPool.execute(new Thread1(lock));
//        threadPool.execute(new Thread2(lock));
//        for (int i = 1; i <= 3; i ++) {
//            final  int task = i;   //10个任务
//            //TimeUnit.SECONDS.sleep(1);
//            threadPool.execute(new Runnable() {    //接受一个Runnable实例
//                public void run() {
//                    System.out.println("线程名字： " + Thread.currentThread().getName() +  "  任务名为： "+task);
//                }
//            });
//        }

        new Thread(new Thread1(lock)).start();
        new Thread(new Thread2(lock)).start();
    }

}
