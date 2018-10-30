package demo.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestRun {
    public static void main(String[] args) throws InterruptedException {

        Object lock = new Object();

        ExecutorService threadPool = Executors.newCachedThreadPool();
        threadPool.execute(new ThreadA(lock));
        threadPool.execute(new ThreadB(lock));
        threadPool.execute(new ThreadC(lock));
//        ThreadA a = new ThreadA(lock);
//        a.start();
//
//        ThreadC c = new ThreadC(lock);
//        c.start();
//
//        ThreadB b = new ThreadB(lock);
//        b.start();
    }
}
