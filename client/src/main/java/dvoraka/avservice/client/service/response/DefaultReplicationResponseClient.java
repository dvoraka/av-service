package dvoraka.avservice.client.service.response;

import dvoraka.avservice.client.transport.ReplicationComponent;
import dvoraka.avservice.common.data.MessageType;
import dvoraka.avservice.common.data.replication.MessageRouting;
import dvoraka.avservice.common.data.replication.ReplicationMessage;
import dvoraka.avservice.common.helper.replication.ReplicationHelper;
import dvoraka.avservice.common.listener.ReplicationMessageListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

import static java.util.Objects.requireNonNull;

/**
 * Default replication response client implementation.
 */
@Service
public class DefaultReplicationResponseClient implements
        ReplicationResponseClient, ReplicationMessageListener, ReplicationHelper {

    private final ReplicationComponent replicationComponent;
    private final String nodeId;

    private static final Logger log = LogManager.getLogger(DefaultReplicationResponseClient.class);

    private static final String CACHE_NAME = "replicationMessageCache";
    public static final int CACHE_TIMEOUT = 60 * 1_000; // one minute

    private CacheManager cacheManager;
    private Cache<String, ReplicationMessageList> messageCache;

    private final Set<ReplicationMessageListener> noResponseListeners;

    private volatile boolean started;
    private volatile boolean running;


    @Autowired
    public DefaultReplicationResponseClient(
            ReplicationComponent replicationComponent,
            String nodeId
    ) {
        this.replicationComponent = requireNonNull(replicationComponent);
        this.nodeId = requireNonNull(nodeId);

        noResponseListeners = new CopyOnWriteArraySet<>();
    }

    @PostConstruct
    public void start() {
        log.debug("Starting ({})...", nodeId);

        synchronized (this) {
            if (isStarted()) {
                log.warn("Service is already started!");
                return;
            }

            setStarted(true);
        }

        log.info("Started ({}).", nodeId);

        // initialize service in a different thread
        CompletableFuture.runAsync(this::initializeCache)
                .thenRun(() -> replicationComponent.addMessageListener(this))
                .thenRunAsync(this::checkTransport)
                .thenRun(() -> log.info("Running."));
    }

    @PreDestroy
    public void stop() {
        log.debug("Stopping ({})...", nodeId);

        synchronized (this) {
            if (!isStarted()) {
                log.info("Service is already stopped.");
                return;
            }

            setStarted(false);
        }

        log.info("Stop ({}).", nodeId);

        replicationComponent.removeMessageListener(this);

        if (cacheManager != null) {
            cacheManager.close();
        }
    }

    private void initializeCache() {
        log.debug("Initializing cache ({})...", nodeId);
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(CACHE_NAME, getCacheConfiguration())
                .build(true);
        messageCache = cacheManager.getCache(
                CACHE_NAME, String.class, ReplicationMessageList.class);
        log.info("Cache initialized.");
    }

    private CacheConfiguration<String, ReplicationMessageList> getCacheConfiguration() {
        final long heapEntries = 10;

        return CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                        String.class,
                        ReplicationMessageList.class,
                        ResourcePoolsBuilder.heap(heapEntries))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(
                        Duration.ofMillis(CACHE_TIMEOUT)))
                .build();
    }

    private void checkTransport() {
        log.debug("Checking transport...");

        final int waitTime = 10;
        final int responseTime = 100;
        ReplicationMessageList emptyList = new ReplicationMessageList();

        while (!isRunning() && isStarted()) {
            ReplicationMessage diagnosticsMsg = createDiagnosticsMessage(nodeId);
            replicationComponent.send(diagnosticsMsg);

            Optional<ReplicationMessageList> response =
                    getResponseWait(diagnosticsMsg.getId(), responseTime);

            if (response.orElse(emptyList).stream().count() > 0) {
                setRunning(true);
                log.info("Transport started.");
            } else {
                try {
                    log.debug("Transport waiting for start...");
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    return;
                }
            }
        }
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
    public Optional<ReplicationMessageList> getResponseWait(
            String id, long minWaitTime, long maxWaitTime) {

        try {
            Thread.sleep(minWaitTime);
        } catch (InterruptedException e) {
            log.warn("Sleeping interrupted!", e);
            Thread.currentThread().interrupt();

            return Optional.empty();
        }

        return Optional.ofNullable(checkGetResponse(id, maxWaitTime - minWaitTime));
    }

    @Override
    public Optional<ReplicationMessageList> getResponseWaitSize(
            String id, long maxWaitTime, int size) {
        final long start = System.currentTimeMillis();

        ReplicationMessageList result = null;
        while (result == null
                || result.stream().count() != size) {

            result = checkGetResponse(id, maxWaitTime);

            if (!sleep(start, maxWaitTime)) {
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
        final long sleepTime = 2;

        if ((System.currentTimeMillis() - start) > maxTime) {

            return false;
        } else {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                log.warn("Sleeping interrupted!", e);
                Thread.currentThread().interrupt();

                return false;
            }

            return true;
        }
    }

    @Override
    public void onMessage(ReplicationMessage response) {
        log.debug("On message ({}): {}", nodeId, response);

        // filter out own messages except diagnostics messages
        if (nodeId.equals(response.getFromId()) && response.getType() != MessageType.DIAGNOSTICS) {
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

    @Override
    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }
}
