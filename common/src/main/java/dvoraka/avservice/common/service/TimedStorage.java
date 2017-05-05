package dvoraka.avservice.common.service;

import dvoraka.avservice.common.CustomThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Timed storage for a temporary data saving. It is up to client to delete the data but
 * there is a max time too. If you forget to clean, everything is still fine.
 * <p>
 * Thread-safe.
 */
@Service
@Scope("prototype")
public class TimedStorage<T> implements ExecutorServiceHelper {

    private static final Logger log = LogManager.getLogger(TimedStorage.class);

    /**
     * Default maximum time in milliseconds.
     */
    public static final long MAX_TIME = 60_000L;
    @SuppressWarnings("checkstyle:ConstantName")
    private static final AtomicLong storageNumber = new AtomicLong();
    /**
     * Maximum time in milliseconds.
     */
    private long maxTime;
    private volatile boolean running;
    private final ConcurrentHashMap<T, Long> storage;
    private final ExecutorService executorService;


    public TimedStorage() {
        this(MAX_TIME);
    }

    public TimedStorage(long maxTime) {
        this.maxTime = maxTime;
        storage = new ConcurrentHashMap<>();

        running = true;
        String name = "storage-" + storageNumber.getAndIncrement() + "-cleaner-";
        ThreadFactory threadFactory = new CustomThreadFactory(name);
        executorService = Executors.newSingleThreadExecutor(threadFactory);
        executorService.execute(this::cleanStorage);
    }

    private void cleanStorage() {
        while (running) {
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
        return Optional.ofNullable(data)
                .map(storage::remove)
                .orElse(0L);
    }

    public long size() {
        return storage.size();
    }

    public long getMaxTime() {
        return maxTime;
    }

    @PreDestroy
    public void stop() {
        if (!running) {
            return;
        }

        log.info("Stopping storage...");

        running = false;
        final long timeout = 5;
        shutdownAndAwaitTermination(executorService, timeout, log);
        log.info("Storage stopped.");
    }
}
