package dvoraka.avservice.common.service;

import dvoraka.avservice.common.CustomThreadFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Timed storage for a temporary data saving. It is up to client to delete the data but
 * there is a max time too. If you forget to clean, everything is still fine.
 * <p>
 * Thread-safe.
 */
public class TimedStorage<T> {

    private static final long MAX_TIME = 60_000;

    private long maxTime;
    private final ConcurrentHashMap<T, Long> storage;
    private final ExecutorService executorService;

    public TimedStorage() {
        this(MAX_TIME);
    }

    public TimedStorage(long maxTime) {
        this.maxTime = maxTime;
        storage = new ConcurrentHashMap<>();

        ThreadFactory threadFactory = new CustomThreadFactory("storage-cleaner-");
        executorService = Executors.newSingleThreadExecutor(threadFactory);
        executorService.execute(this::cleanStorage);
    }

    private void cleanStorage() {
        while (true) {
            long now = System.currentTimeMillis();
            storage.entrySet()
                    .removeIf(entry -> (entry.getValue() + maxTime) < now);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void put(T data) {
        storage.put(data, System.currentTimeMillis());
    }

    public boolean contains(T data) {
        return storage.containsKey(data);
    }

    public long remove(T data) {
        return storage.remove(data);
    }

    public long size() {
        return storage.size();
    }

    public void stop() {
        //TODO: stop the service
    }
}
