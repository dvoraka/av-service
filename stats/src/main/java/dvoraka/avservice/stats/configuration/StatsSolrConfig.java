package dvoraka.avservice.stats.configuration;

import dvoraka.avservice.db.configuration.SolrConfig;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.stats.Messages;
import dvoraka.avservice.stats.MessagesSolr;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Solr stats configuration.
 */
@Configuration
@Import(SolrConfig.class)
@Profile("stats-solr")
public class StatsSolrConfig {

    @Bean
    public Messages messages(MessageInfoService messageInfoService) {
        return new MessagesSolr(messageInfoService);
    }
}
