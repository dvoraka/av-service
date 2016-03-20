package dvoraka.avservice.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Custom thread factory.
 */
public class CustomThreadFactory implements ThreadFactory {

    private String poolName;
    private AtomicLong counter = new AtomicLong(0);

    public CustomThreadFactory(String name) {
        poolName = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(poolName + counter.getAndIncrement());

        return thread;
    }
}
