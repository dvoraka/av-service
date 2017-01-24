package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.configuration.ServiceConfig;
import dvoraka.avservice.rest.RestStrategy;
import dvoraka.avservice.rest.controller.AvController;
import dvoraka.avservice.rest.controller.AvStatsController;
import dvoraka.avservice.rest.service.AvRestService;
import dvoraka.avservice.rest.service.DefaultAvRestService;
import dvoraka.avservice.stats.StatsService;
import dvoraka.avservice.stats.configuration.StatsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

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
public class SpringWebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public AvRestService restService(RestStrategy restStrategy) {
        return new DefaultAvRestService(restStrategy);
    }

    @Bean
    public AvController avController(AvRestService restService) {
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
