package dvoraka.avservice.db.configuration;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

/**
 * Solr Spring configuration.
 */
@Configuration
@EnableSolrRepositories(basePackages = "dvoraka.avservice.db.repository")
public class SolrConfig {

    @Bean
    public SolrClient solrClient() {
        return new HttpSolrClient("http://localhost:8983/solr/test");
    }

    @Bean
    public SolrTemplate solrTemplate(SolrClient solrClient) {
        return new SolrTemplate(solrClient);
    }
}
