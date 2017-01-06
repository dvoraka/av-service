package dvoraka.avservice.stats.configuration;

import dvoraka.avservice.stats.DefaultStatsService;
import dvoraka.avservice.stats.Messages;
import dvoraka.avservice.stats.StatsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Main statistics configuration.
 */
@Configuration
@Import({
        StatsSolrConfig.class
})
@Profile("stats")
public class StatsConfig {

    @Bean
    public StatsService statsService(Messages messages) {
        return new DefaultStatsService(messages);
    }
}
