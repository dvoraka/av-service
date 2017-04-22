package dvoraka.avservice.client.service.response;

import dvoraka.avservice.client.ReplicationComponent;
import dvoraka.avservice.common.ReplicationMessageListener;
import dvoraka.avservice.common.data.MessageRouting;
import dvoraka.avservice.common.data.ReplicationMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * Default replication response client implementation.
 */
@Service
public class DefaultReplicationResponseClient implements
        ReplicationResponseClient, ReplicationMessageListener {

    private final ReplicationComponent replicationComponent;
    private final String nodeId;

    private static final Logger log = LogManager.getLogger(DefaultReplicationResponseClient.class);

    private static final String CACHE_NAME = "replicationMessageCache";
    public static final int CACHE_TIMEOUT = 60 * 1_000; // one minute

    private CacheManager cacheManager;
    private Cache<String, ReplicationMessageList> messageCache;

    private Set<ReplicationMessageListener> noResponseListeners;

    private volatile boolean started;


    @Autowired
    public DefaultReplicationResponseClient(
            ReplicationComponent replicationComponent,
            String nodeId
    ) {
        this.replicationComponent = requireNonNull(replicationComponent);
        this.nodeId = nodeId;

        noResponseListeners = new CopyOnWriteArraySet<>();
    }

    @PostConstruct
    public synchronized void start() {
        if (isStarted()) {
            log.info("Service is already started.");
            return;
        }

        log.info("Start.");
        setStarted(true);
        initializeCache();
        replicationComponent.addReplicationMessageListener(this);
    }

    @PreDestroy
    public synchronized void stop() {
        if (!isStarted()) {
            log.info("Service is already stopped.");
            return;
        }

        log.info("Stop.");
        setStarted(false);
        replicationComponent.removeReplicationMessageListener(this);
        cacheManager.close();
    }

    private void initializeCache() {
        log.debug("Initializing cache ({})...", nodeId);
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(CACHE_NAME, getCacheConfiguration())
                .build(true);
        messageCache = cacheManager.getCache(
                CACHE_NAME, String.class, ReplicationMessageList.class);
    }

    private CacheConfiguration<String, ReplicationMessageList> getCacheConfiguration() {
        final long heapEntries = 10;

        return CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                        String.class,
                        ReplicationMessageList.class,
                        ResourcePoolsBuilder.heap(heapEntries))
                .withExpiry(Expirations.timeToLiveExpiration(
                        new Duration(CACHE_TIMEOUT, TimeUnit.MILLISECONDS)))
                .build();
    }

    public boolean isStarted() {
        return started;
    }

    private void setStarted(boolean started) {
        this.started = started;
    }

    @Override
    public ReplicationMessageList getResponse(String id) {
        log.debug("Asking for ({}): {}", nodeId, id);
        return messageCache.get(id);
    }

    @Override
    public Optional<ReplicationMessageList> getResponseWait(String id, long waitTime) {
        ReplicationMessageList result = checkGetResponse(id, waitTime);

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<ReplicationMessageList> getResponseWait(String id, long waitTime, int size) {
        final long start = System.currentTimeMillis();

        ReplicationMessageList result;
        while (true) {
            result = checkGetResponse(id, waitTime);

            if (result != null && result.stream().count() == size) {
                break;
            }

            if (!sleep(start, waitTime)) {
                break;
            }
        }

        return Optional.ofNullable(result);
    }

    private ReplicationMessageList checkGetResponse(String id, long maxTime) {
        final long start = System.currentTimeMillis();

        ReplicationMessageList result;
        while (true) {
            result = getResponse(id);
            if (result != null || !sleep(start, maxTime)) {
                break;
            }
        }

        return result;
    }

    private boolean sleep(long start, long maxTime) {
        final long sleepTime = 200;

        if ((System.currentTimeMillis() - start) > maxTime) {

            return false;
        } else {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                log.warn("Sleeping interrupted!", e);
                Thread.currentThread().interrupt();
            }

            return true;
        }
    }

    @Override
    public void onMessage(ReplicationMessage response) {
        log.debug("On message ({}): {}", nodeId, response);

        // filter out own messages
        if (nodeId.equals(response.getFromId())) {
            log.debug("Filtering 1 ({}): {}", nodeId, response);

            return;
        }

        // filter out unicast messages for other nodes
        if (response.getRouting() == MessageRouting.UNICAST
                && !nodeId.equals(response.getToId())) {
            log.debug("Filtering 2 ({}): {}", nodeId, response);

            return;
        }

        // filter out and forward messages without a correlation ID
        String corrId = response.getCorrelationId();
        if (corrId == null) { // not response
            log.debug("No correlation ID ({}): {}", nodeId, response);
            if (!nodeId.equals(response.getFromId())) {
                noResponseListeners.forEach(listener -> listener.onMessage(response));
            }

            return;
        }

        ReplicationMessageList messageList;
        if (messageCache.containsKey(corrId)) {
            messageList = messageCache.get(corrId);
        } else {
            messageList = new ReplicationMessageList();
        }

        messageList.add(response);
        log.debug("Adding to cache ({}): {}", nodeId, response);
        messageCache.put(corrId, messageList);
    }

    @Override
    public void addNoResponseMessageListener(ReplicationMessageListener listener) {
        noResponseListeners.add(listener);
    }

    @Override
    public void removeNoResponseMessageListener(ReplicationMessageListener listener) {
        noResponseListeners.remove(listener);
    }
}
