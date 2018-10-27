package demo.test;

public class TestRun {
    public static void main(String[] args) {

        try {
            Object lock = new Object();

            ThreadA a = new ThreadA(lock);
            a.start();



            ThreadB b = new ThreadB(lock);
            b.start();
            Thread.sleep(20*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
