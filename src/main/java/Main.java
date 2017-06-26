import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import rabbitmq.publisher.IMessagePublisher;
import rabbitmq.factory.MessagePublisherFactory;
import rabbitmq.enums.CacheType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Arpit
 */
public class Main {

    public static void main(String[] args) throws Exception {

        // Providing breathing space for setup.
        Thread.sleep(10000);

        String message = "Hello AMQP";

        IMessagePublisher messagePublisher = MessagePublisherFactory.getPublisher(CacheType.CHANNEL_CACHE);
        BasicThreadFactory factory = new BasicThreadFactory.Builder()
                                            .namingPattern("arpit-rmq-%d")
                                            .priority(Thread.NORM_PRIORITY)
                                            .build();

        ExecutorService threadPool = Executors.newFixedThreadPool(5, factory);

        for (int i = 1; i <= 1000000; i++) {
            System.out.println(i);
            int finalI = i;

//            if (i % 5 == 0) {
//                Thread.sleep(1000);
//            }

            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    messagePublisher.publishMessage("testExchange", "testRoutingKey1", message + String.valueOf(finalI));
                }
            });
        }

        // Wait for 1 minutes to ensure already submitted tasks get executed
        threadPool.awaitTermination(1, TimeUnit.MINUTES);
        threadPool.shutdown();

        System.out.println("Thread pool shut down. Load on rabbit-server now.");
    }

}
