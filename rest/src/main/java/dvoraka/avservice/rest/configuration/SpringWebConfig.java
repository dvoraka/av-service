package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.configuration.ServiceConfig;
import dvoraka.avservice.rest.RestStrategy;
import dvoraka.avservice.rest.controller.AvController;
import dvoraka.avservice.rest.controller.AvStatsController;
import dvoraka.avservice.rest.service.DefaultRestService;
import dvoraka.avservice.rest.service.RestService;
import dvoraka.avservice.stats.StatsService;
import dvoraka.avservice.stats.configuration.StatsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.validation.Validator;

/**
 * Spring REST configuration.
 */
@EnableWebMvc
@Configuration
@Profile("rest")
@Import({
        RestConfig.class,
        ServiceConfig.class,
        StatsConfig.class
})
public class SpringWebConfig {

    @Bean
    public RestService restService(RestStrategy restStrategy) {
        return new DefaultRestService(restStrategy);
    }

    @Bean
    public AvController avController(RestService restService) {
        return new AvController(restService);
    }

    @Bean
    @Profile("stats")
    public AvStatsController avStatsController(StatsService statsService) {
        return new AvStatsController(statsService);
    }

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(Validator validator) {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator);

        return processor;
    }
}
