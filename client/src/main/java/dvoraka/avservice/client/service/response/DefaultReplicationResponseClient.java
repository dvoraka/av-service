package dvoraka.avservice.client.service.response;

import dvoraka.avservice.client.ReplicationComponent;
import dvoraka.avservice.common.ReplicationMessageListener;
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
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * Default replication response client implementation.
 */
@Service
public class DefaultReplicationResponseClient implements
        ReplicationResponseClient, ReplicationMessageListener {

    private final ReplicationComponent replicationComponent;

    private static final Logger log = LogManager.getLogger(DefaultReplicationResponseClient.class);

    private static final String CACHE_NAME = "replicationMessageCache";
    public static final int CACHE_TIMEOUT = 60 * 1_000; // one minute

    private CacheManager cacheManager;
    private Cache<String, ReplicationMessageList> messageCache;

    private volatile boolean started;


    @Autowired
    public DefaultReplicationResponseClient(ReplicationComponent replicationComponent) {
        this.replicationComponent = requireNonNull(replicationComponent);
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
        return messageCache.get(id);
    }

    @Override
    public ReplicationMessageList getResponseWait(String id, long waitTime) {
        final long start = System.currentTimeMillis();
        final int sleepTime = 100;

        ReplicationMessageList result;
        while (true) {
            result = getResponse(id);
            if (result != null) {
                break;
            }

            if ((System.currentTimeMillis() - start) > waitTime) {
                break;
            } else {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    log.warn("Sleeping interrupted!", e);
                    Thread.currentThread().interrupt();
                }
            }
        }

        return result;
    }


    @Override
    public void onMessage(ReplicationMessage response) {
        log.debug("On message: {}", response);
//        messageCache.put(response.getCorrelationId(), response);
    }
}
