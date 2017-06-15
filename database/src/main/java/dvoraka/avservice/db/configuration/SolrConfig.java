package dvoraka.avservice.db.configuration;

import dvoraka.avservice.db.repository.solr.SolrMessageInfoRepository;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.db.service.SolrMessageInfoService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

/**
 * Solr Spring configuration.
 */
@Configuration
@EnableSolrRepositories(basePackages = "dvoraka.avservice.db.repository.solr")
@Profile("db-solr")
public class SolrConfig {

    @Bean
    public SolrClient solrClient() {
        return new HttpSolrClient.Builder()
                .withBaseSolrUrl("http://localhost:8983/solr/")
                .build();
    }

    @Bean
    public SolrTemplate solrTemplate(SolrClient solrClient) {
        return new SolrTemplate(solrClient);
    }

    @Bean
    public MessageInfoService messageInfoService(SolrMessageInfoRepository messageInfoRepository) {
        return new SolrMessageInfoService(messageInfoRepository);
    }
}
