package demo;

import java.util.concurrent.*;

//java简单实现异步队列:使用生产者与消费者模型
public class MyAsynQueue {

    // http://www.importnew.com/22519.html
    // 模拟消息队列订阅者 同时4个线程处理，任务提交者
    private static final ThreadPoolExecutor THREAD_POOL = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    // 模拟消息队列生产者,单一线程， 处理者
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    // 用于判断是否关闭订阅
    private static volatile boolean isClose = false;
    static int taskId=0;
    static int taskId2=0;
    public static void main(String[] args) throws InterruptedException {
        //保存任务队列
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(100);
        BlockingQueue<String> queue2 = new ArrayBlockingQueue<String>(100);
        producer(queue,queue2);
        consumer(queue, queue2);
//        Thread.sleep(6000);
//        exitALl();
//        System.out.println("all finish!");
    }


    private static boolean exitALl() {
        if(taskId>10)
        {

            THREAD_POOL.shutdown();
            SCHEDULED_EXECUTOR_SERVICE.shutdown();
            return true;
        }else{
            return false;
        }
    }


    // 模拟消息队列生产者
    private static void producer(final BlockingQueue queue, final BlockingQueue queue2) {

        // 每200毫秒向队列中放入一个消息
//        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new Runnable() {
//            public void run() {
//                taskId++;
//                queue.offer("taskId="+taskId);
//                //exitALl();
//
//            }
//        }, 0L, 200L, TimeUnit.MILLISECONDS);
        SCHEDULED_EXECUTOR_SERVICE.execute(new Runnable() {
            @Override
            public void run() {
                while (true){
                    taskId++;
                    queue.offer("taskId="+taskId);
                    System.out.println("taskId="+taskId);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    taskId2++;
                    queue2.offer("taskId="+taskId2);
                    System.out.println("taskId="+taskId2);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // 模拟消息队列消费者 生产者每秒生产5个 消费者4个线程消费1个1秒 每秒积压1个
    private static void consumer(final BlockingQueue queue, final BlockingQueue queue2) throws InterruptedException {
        //while (!isClose)
        while(true)
        {
            // 从队列中拿到消息
            final String msg = (String) queue.take();
            final String msg2 = (String) queue2.take();
            // 放入线程池处理
            THREAD_POOL.execute(new Runnable() {
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName());

                        TimeUnit.MILLISECONDS.sleep(50L);
                        System.out.println(msg+" 任务处理完毕！");
                        System.out.println(msg2+" 任务2处理完毕！");


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            getPoolBacklogSize();
            if(exitALl())
            {
                break;
            }

        }
    }

    // 查看线程池堆积消息个数
    private static long getPoolBacklogSize() {
        long backlog = THREAD_POOL.getTaskCount() - THREAD_POOL.getCompletedTaskCount();
        System.out.println(String.format("[%s]THREAD_POOL 积压的任务:%s", System.currentTimeMillis(), backlog));
        return backlog;
    }


}