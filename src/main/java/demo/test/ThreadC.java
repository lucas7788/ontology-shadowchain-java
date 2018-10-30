package demo.test;

public class ThreadC extends Thread {
    private Object lock;

    public ThreadC(Object lock) {
        super();
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                synchronized (lock){
                    MyList.add("testC" + i);
                    lock.notify();
                }
                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
