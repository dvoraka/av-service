package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.configuration.ServiceConfig;
import dvoraka.avservice.rest.controller.CheckController;
import dvoraka.avservice.rest.controller.FileController;
import dvoraka.avservice.rest.controller.MainController;
import dvoraka.avservice.rest.controller.StatsController;
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
    public MainController mainController(RestService avRestService) {
        return new MainController(avRestService);
    }

    @Bean
    public CheckController checkController(RestService avRestService) {
        return new CheckController(avRestService);
    }

    @Bean
    public FileController fileController(RestService avRestService) {
        return new FileController(avRestService);
    }

    @Bean
    @Profile("stats")
    public StatsController statsController(StatsService statsService) {
        return new StatsController(statsService);
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
