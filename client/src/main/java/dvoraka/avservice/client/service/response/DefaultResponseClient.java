package dvoraka.avservice.client.service.response;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
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
 * Default response client implementation.
 */
@Service
public class DefaultResponseClient implements ResponseClient, AvMessageListener {

    private final ServerComponent serverComponent;

    private static final Logger log = LogManager.getLogger(DefaultResponseClient.class);

    private static final String CACHE_NAME = "messageCache";
    public static final int CACHE_TIMEOUT = 60 * 1_000; // one minute

    private CacheManager cacheManager;
    private Cache<String, AvMessage> messageCache;

    private volatile boolean started;


    @Autowired
    public DefaultResponseClient(ServerComponent serverComponent) {
        this.serverComponent = requireNonNull(serverComponent);
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
        serverComponent.addAvMessageListener(this);
    }

    @PreDestroy
    public synchronized void stop() {
        if (!isStarted()) {
            log.info("Service is already stopped.");
            return;
        }

        log.info("Stop.");
        setStarted(false);
        serverComponent.removeAvMessageListener(this);
        cacheManager.close();
    }

    private void initializeCache() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(CACHE_NAME, getCacheConfiguration())
                .build(true);
        messageCache = cacheManager.getCache(CACHE_NAME, String.class, AvMessage.class);
    }

    private CacheConfiguration<String, AvMessage> getCacheConfiguration() {
        final long heapEntries = 10;

        return CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                        String.class,
                        AvMessage.class,
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
    public AvMessage getResponse(String id) {
        return messageCache.get(id);
    }

    @Override
    public void onAvMessage(AvMessage response) {
        log.debug("On message: {}", response);
        messageCache.put(response.getCorrelationId(), response);
    }
}
