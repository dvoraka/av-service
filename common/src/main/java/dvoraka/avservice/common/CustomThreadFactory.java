package dvoraka.avservice.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.requireNonNull;

/**
 * Custom thread factory.
 */
public final class CustomThreadFactory implements ThreadFactory {

    private final String poolName;
    private final AtomicLong counter;


    /**
     * Creates a custom thread factory with a given base name. Every thread will have this name
     * and a different number starting with 0 as a suffix.
     *
     * @param name the thread name
     */
    public CustomThreadFactory(String name) {
        poolName = requireNonNull(name);
        counter = new AtomicLong();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(poolName + counter.getAndIncrement());

        return thread;
    }
}
