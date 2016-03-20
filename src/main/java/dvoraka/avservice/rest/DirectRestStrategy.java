package dvoraka.avservice.rest;

import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.data.AVMessage;
import dvoraka.avservice.data.MessageStatus;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * Implementation for a local calling.
 */
public class DirectRestStrategy implements RestStrategy {

    @Autowired
    private MessageProcessor messageProcessor;

    CacheManager cacheManager;
    Cache<String, AVMessage> messageCache;


    public DirectRestStrategy() {

        initializeCache();
    }
    private void initializeCache() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("restCache",
                        CacheConfigurationBuilder
                                .newCacheConfigurationBuilder(String.class, AVMessage.class)
                                .withExpiry(Expirations.timeToLiveExpiration(
                                        new Duration(1000, TimeUnit.MILLISECONDS)))
                                .build())
                .build(true);

        messageCache = cacheManager.getCache("restCache", String.class, AVMessage.class);
    }

    @Override
    public MessageStatus messageStatus(String id) {
        return messageProcessor.messageStatus(id);
    }

    @Override
    public MessageStatus messageStatus(String id, String serviceId) {
        return messageStatus(id);
    }

    @Override
    public String messageServiceId(String id) {
        return null;
    }

    @Override
    public void messageCheck(AVMessage message) {
        messageProcessor.sendMessage(message);
    }

    @Override
    public AVMessage getResponse(String id) {
        // TODO: check ID
        if (messageProcessor.hasProcessedMessage()) {
            return messageProcessor.getProcessedMessage();
        } else {
            return null;
        }
    }

    @Override
    public void stop() {
        messageProcessor.stop();

        cacheManager.close();
    }
}
