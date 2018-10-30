package demo.testexector;

public class Thread1 implements Runnable{
    private Object lock;
    public Thread1(Object lock){
        this.lock = lock;
    }
    @Override
    public void run() {
        System.out.println("test1");
        for(int i=0;i<10;i++){
            synchronized (lock){
                MyList.add();
                System.out.println(MyList.size());
                lock.notify();
            }
        }
    }
}
