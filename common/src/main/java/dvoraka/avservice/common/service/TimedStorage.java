package dvoraka.avservice.common.service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Timed storage for data.
 */
public class TimedStorage<T> {

    private final ConcurrentHashMap<T, Long> storage;


    public TimedStorage() {
        storage = new ConcurrentHashMap<T, Long>();
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
}
