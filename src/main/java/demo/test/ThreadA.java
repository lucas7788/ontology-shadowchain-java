package demo.test;

public class ThreadA extends Thread {
    private Object lock;

    public ThreadA(Object lock) {
        super();
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            synchronized (lock) {
                if (MyList.size() != 5) {
                    System.out.println("wait begin "
                            + System.currentTimeMillis());
                    lock.wait();
                    Thread.sleep(9000);
                    System.out.println("wait end  "
                            + System.currentTimeMillis());
                }else {
                    System.out.println("=====5");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
