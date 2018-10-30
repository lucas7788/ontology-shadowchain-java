package demo.testexector;

public class Thread2 implements Runnable{
    private Object lock;
    public Thread2(Object lock){
        this.lock = lock;
    }
    @Override
    public void run() {
        for(int i=0;i<10;i++){
            synchronized (lock){
                try {
                    lock.wait();
                    System.out.println(MyList.size() + "thread2");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
