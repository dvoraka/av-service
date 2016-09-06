package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.DefaultMessageProcessor;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.configuration.ServiceConfig;
import dvoraka.avservice.rest.controller.AvRestController;
import dvoraka.avservice.rest.DirectRestStrategy;
import dvoraka.avservice.rest.RestStrategy;
import dvoraka.avservice.rest.service.DefaultRestService;
import dvoraka.avservice.rest.service.RestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Spring REST configuration.
 */
@EnableWebMvc
@Configuration
@Profile("rest")
@Import({RestSecurityConfig.class, ServiceConfig.class})
public class SpringWebConfig extends WebMvcConfigurerAdapter {


    @Bean
    public AvRestController avController() {
        return new AvRestController();
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
    public MessageProcessor restMessageProcessor() {
        final int threads = 20;
        return new DefaultMessageProcessor(threads, "service1");
    }
}
