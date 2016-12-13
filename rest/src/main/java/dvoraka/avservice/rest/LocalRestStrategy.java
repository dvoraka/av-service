package dvoraka.avservice.rest;

import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
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
 * Local REST strategy. Uses directly the message processor.
 */
@Service
public class LocalRestStrategy implements RestStrategy, AvMessageListener {

    private final MessageProcessor restMessageProcessor;

    private static final Logger log = LogManager.getLogger(LocalRestStrategy.class.getName());

    private CacheManager cacheManager;
    private Cache<String, AvMessage> messageCache;


    @Autowired
    public LocalRestStrategy(MessageProcessor restMessageProcessor) {
        this.restMessageProcessor = restMessageProcessor;
        initializeCache();
    }

    private void initializeCache() {
        final long expirationTime = 10_000;
        final long heapEntries = 10;
        CacheConfiguration<String, AvMessage> configuration = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                        String.class, AvMessage.class, ResourcePoolsBuilder.heap(heapEntries))
                .withExpiry(Expirations.timeToLiveExpiration(
                        new Duration(expirationTime, TimeUnit.MILLISECONDS)))
                .build();

        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("restCache", configuration)
                .build(true);

        messageCache = cacheManager.getCache("restCache", String.class, AvMessage.class);
    }

    @Override
    public MessageStatus messageStatus(String id) {
        return restMessageProcessor.messageStatus(id);
    }

    @Override
    public MessageStatus messageStatus(String id, String serviceId) {
        return messageStatus(id);
    }

    @Override
    public String messageServiceId(String id) {
        // null means local for now
        return null;
    }

    @Override
    public void messageCheck(AvMessage message) {
        restMessageProcessor.sendMessage(message);
    }

    @Override
    public AvMessage getResponse(String id) {
        return messageCache.get(id);
    }

    @PostConstruct
    @Override
    public void start() {
        log.debug("Starting cache updating...");
        restMessageProcessor.addProcessedAVMessageListener(this);
    }

    @Override
    @PreDestroy
    public void stop() {
        restMessageProcessor.stop();
        cacheManager.close();
    }

    @Override
    public void onAvMessage(AvMessage message) {
        messageCache.put(message.getCorrelationId(), message);
        log.debug("Saving message: " + message.getId());
    }
}
