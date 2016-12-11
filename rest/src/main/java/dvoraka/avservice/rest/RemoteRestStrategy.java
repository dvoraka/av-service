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
 * Remote REST strategy. Receives requests over REST and sends it along over network.
 */
@Service
public class RemoteRestStrategy implements RestStrategy, AvMessageListener {

    private final ServerComponent serverComponent;

    private static final Logger log = LogManager.getLogger(RemoteRestStrategy.class);

    private static final String CACHE_NAME = "remoteRestCache";

    private final TimedStorage<String> processingMsgs = new TimedStorage<>();
    private final TimedStorage<String> processedMsgs = new TimedStorage<>();

    // messages caching
    private CacheManager cacheManager;
    private Cache<String, AvMessage> messageCache;


    @Autowired
    public RemoteRestStrategy(ServerComponent serverComponent) {
        this.serverComponent = serverComponent;
    }

    private void initializeCache() {
        final long expirationTime = 10_000;
        final long heapEntries = 10;

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

        return null;
    }

    @Override
    public String messageServiceId(String id) {
        return null;
    }

    @Override
    public void messageCheck(AvMessage message) {
        log.debug("Checking: {}", message);
        processingMsgs.put(message.getId());
        serverComponent.sendMessage(message);
    }

    @Override
    public AvMessage getResponse(String id) {
        return messageCache.get(id);
    }

    @PostConstruct
    @Override
    public void start() {
        log.info("Started.");
        initializeCache();
        serverComponent.addAvMessageListener(this);
    }

    @PreDestroy
    @Override
    public void stop() {
        log.info("Stopped.");
        serverComponent.removeAvMessageListener(this);
        cacheManager.close();
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
}
