package dvoraka.avservice.rest;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.common.service.TimedStorage;
import dvoraka.avservice.server.ServerComponent;
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

/**
 * Remote REST strategy. Receives requests through REST and sends it along over network.
 */
@Service
public class RemoteRestStrategy implements RestStrategy, AvMessageListener {

    private final ServerComponent serverComponent;

    private static final Logger log = LogManager.getLogger(RemoteRestStrategy.class);

    private static final String CACHE_NAME = "remoteRestCache";

    private final TimedStorage<String> processingMsgs = new TimedStorage<>();
    private final TimedStorage<String> processedMsgs = new TimedStorage<>();

    private volatile boolean started;

    // message caching
    private CacheManager cacheManager;
    private Cache<String, AvMessage> messageCache;


    @Autowired
    public RemoteRestStrategy(ServerComponent serverComponent) {
        this.serverComponent = serverComponent;
    }

    private void initializeCache() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(CACHE_NAME, getCacheConfiguration())
                .build(true);

        messageCache = cacheManager.getCache(CACHE_NAME, String.class, AvMessage.class);
    }

    private CacheConfiguration<String, AvMessage> getCacheConfiguration() {
        final long expirationTime = 10_000;
        final long heapEntries = 10;

        return CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                        String.class, AvMessage.class, ResourcePoolsBuilder.heap(heapEntries))
                .withExpiry(Expirations.timeToLiveExpiration(
                        new Duration(expirationTime, TimeUnit.MILLISECONDS)))
                .build();
    }

    @Override
    public MessageStatus messageStatus(String id) {
        return messageStatus(id, null);
    }

    @Override
    public MessageStatus messageStatus(String id, String serviceId) {
        if (processedMsgs.contains(id)) {
            return MessageStatus.PROCESSED;
        } else if (processingMsgs.contains(id)) {
            return MessageStatus.PROCESSING;
        }

        return MessageStatus.UNKNOWN;
    }

    @Override
    public String messageServiceId(String id) {
        return null;
    }

    @Override
    public void messageCheck(AvMessage message) {
        log.debug("Checking: {}", message);
        processingMsgs.put(message.getId());
        serverComponent.sendAvMessage(message);
    }

    @Override
    public AvMessage getResponse(String id) {
        return messageCache.get(id);
    }

    @PostConstruct
    @Override
    public void start() {
        if (!isStarted()) {
            log.info("Starting.");
            setStarted(true);

            initializeCache();
            serverComponent.addAvMessageListener(this);
        } else {
            log.info("Service is already started.");
        }
    }

    @PreDestroy
    @Override
    public void stop() {
        if (isStarted()) {
            log.info("Stopping.");
            setStarted(false);

            serverComponent.removeAvMessageListener(this);

            processingMsgs.stop();
            processedMsgs.stop();

            cacheManager.close();
        } else {
            log.info("Service is already stopped.");
        }
    }

    @Override
    public void onAvMessage(AvMessage response) {
        log.debug("REST on message: {}", response);

        // skip other messages
        if (processingMsgs.contains(response.getCorrelationId())) {
            log.debug("Saving response: {}", response);

            messageCache.put(response.getCorrelationId(), response);
            processedMsgs.put(response.getCorrelationId());

            processingMsgs.remove(response.getCorrelationId());
        }
    }

    public boolean isStarted() {
        return started;
    }

    private void setStarted(boolean started) {
        this.started = started;
    }
}
