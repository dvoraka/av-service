package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.rest.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

/**
 * Rest client configuration.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
@Profile("rest-client")
public class RestClientConfig {

    @Value("${avservice.rest.url}")
    private String restUrl;


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestClient restClient() {
        return new RestClient(restUrl);
    }
}
