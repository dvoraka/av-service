package dvoraka.avservice.db.configuration;

import dvoraka.avservice.db.service.DummyMessageInfoService;
import dvoraka.avservice.db.service.MessageInfoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * No database Spring configuration for disabling DB.
 */
@Configuration
@Profile("no-db")
public class NoDbConfig {

    @Bean
    public MessageInfoService messageInfoService() {
        return new DummyMessageInfoService();
    }
}
