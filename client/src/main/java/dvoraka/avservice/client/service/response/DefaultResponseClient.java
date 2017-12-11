package dvoraka.avservice.client.service.response;

import dvoraka.avservice.client.transport.AvNetworkComponent;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.InfoSource;
import dvoraka.avservice.common.listener.AvMessageListener;
import dvoraka.avservice.db.service.MessageInfoService;
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
import java.util.concurrent.TimeoutException;

import static java.util.Objects.requireNonNull;

/**
 * Default response client implementation.
 */
@Service
public class DefaultResponseClient implements ResponseClient, AvMessageListener {

    private final AvNetworkComponent avNetworkComponent;
    private final MessageInfoService messageInfoService;

    private static final Logger log = LogManager.getLogger(DefaultResponseClient.class);

    private static final String CACHE_NAME = "messageCache";
    public static final int CACHE_TIMEOUT = 60 * 1_000; // one minute

    private final String serviceId;

    private CacheManager cacheManager;
    private Cache<String, AvMessage> messageCache;

    private volatile boolean started;


    @Autowired
    public DefaultResponseClient(
            AvNetworkComponent avNetworkComponent,
            MessageInfoService messageInfoService
    ) {
        this.avNetworkComponent = requireNonNull(avNetworkComponent);
        this.messageInfoService = requireNonNull(messageInfoService);
        serviceId = requireNonNull(avNetworkComponent.getServiceId());
    }

    @PostConstruct
    public synchronized void start() {
        if (isStarted()) {
            log.info("Service is already started.");
            return;
        }

        log.info("Start.");
        initializeCache();
        avNetworkComponent.addMessageListener(this);
        setStarted(true);
    }

    @PreDestroy
    public synchronized void stop() {
        if (!isStarted()) {
            log.info("Service is already stopped.");
            return;
        }

        log.info("Stop.");
        setStarted(false);
        avNetworkComponent.removeMessageListener(this);
        cacheManager.close();
    }

    private void initializeCache() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(CACHE_NAME, getCacheConfiguration())
                .build(true);
        messageCache = cacheManager.getCache(CACHE_NAME, String.class, AvMessage.class);
    }

    private CacheConfiguration<String, AvMessage> getCacheConfiguration() {
        final long heapEntries = 20_000;

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
        if (!isStarted()) {
            throw new IllegalStateException("Client is not started!");
        }

        return messageCache.get(id);
    }

    @Override
    public AvMessage getResponse(String id, long timeout, TimeUnit unit)
            throws InterruptedException, TimeoutException {

        final long start = System.currentTimeMillis();

        AvMessage response;
        final int sleepTime = 10;
        while ((response = getResponse(id)) == null) {

            if ((System.currentTimeMillis() - start) > unit.toMillis(timeout)) {
                throw new TimeoutException();
            }

            TimeUnit.MILLISECONDS.sleep(sleepTime);
        }

        return response;
    }

    @Override
    public void onMessage(AvMessage response) {
        log.debug("On message: {}", response);
        messageCache.put(response.getCorrelationId(), response);
        messageInfoService.save(response, InfoSource.RESPONSE_CACHE, serviceId);
    }
}
