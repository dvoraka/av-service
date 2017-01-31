package dvoraka.avservice.db.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Database module main configuration.
 */
@Configuration
@Import({
        // DB
        DbConfig.class,
        // No DB - dummy
        NoDbConfig.class,
        // Solr
        SolrConfig.class
})
public class DatabaseConfig {
}
