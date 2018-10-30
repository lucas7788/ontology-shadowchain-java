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
            while (true){
                synchronized (lock) {
                    lock.wait();
                    for(int i=0;i<MyList.size();i++){
                        System.out.println(MyList.get(i));
                        MyList.remove(i);
                    }
                    Thread.sleep(1000);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
