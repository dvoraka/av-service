package dvoraka.avservice.configuration;

import dvoraka.avservice.rest.AVController;
import dvoraka.avservice.rest.DirectRestStrategy;
import dvoraka.avservice.rest.RestClient;
import dvoraka.avservice.rest.RestStrategy;
import dvoraka.avservice.service.DefaultRestService;
import dvoraka.avservice.service.RestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Spring REST configuration.
 */
@EnableWebMvc
@Configuration
@Profile("rest")
public class SpringWebConfig extends WebMvcConfigurerAdapter {

    @Value("${avservice.rest.url}")
    String restUrl;


    @Bean
    public AVController avController() {
        return new AVController();
    }

    @Bean
    public RestService restService() {
        return new DefaultRestService();
    }

    @Bean
    public RestStrategy restStrategy() {
        return new DirectRestStrategy();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestClient restClient() {
        return new RestClient(restUrl);
    }
}
