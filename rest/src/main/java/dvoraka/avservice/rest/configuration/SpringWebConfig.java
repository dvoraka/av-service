package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.configuration.ServiceConfig;
import dvoraka.avservice.rest.RestStrategy;
import dvoraka.avservice.rest.controller.AvRestController;
import dvoraka.avservice.rest.service.DefaultRestService;
import dvoraka.avservice.rest.service.RestService;
import dvoraka.avservice.server.configuration.amqp.AmqpConfig;
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
@Import({
        RestLocalConfig.class,
        RestAmqpConfig.class,
        RestSecurityConfig.class,
        ServiceConfig.class,
        AmqpConfig.class
})
public class SpringWebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public RestService restService(RestStrategy restStrategy) {
        return new DefaultRestService(restStrategy);
    }

    @Bean
    public AvRestController avController(RestService restService) {
        return new AvRestController(restService);
    }
}
