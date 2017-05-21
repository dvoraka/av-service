package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.core.configuration.CoreConfig;
import dvoraka.avservice.rest.RestAspect;
import dvoraka.avservice.rest.controller.CheckController;
import dvoraka.avservice.rest.controller.FileController;
import dvoraka.avservice.rest.controller.MainController;
import dvoraka.avservice.rest.controller.StatsController;
import dvoraka.avservice.rest.service.RestService;
import dvoraka.avservice.stats.StatsService;
import dvoraka.avservice.stats.configuration.StatsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
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
@EnableAspectJAutoProxy
@Profile("rest")
@Import({
        RestConfig.class,

        CoreConfig.class,
        StatsConfig.class,

        RestAspect.class
})
public class SpringWebConfig {

    @Value("${avservice.serviceId}")
    private String serviceId;


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

    @Bean
    public String getServiceId() {
        return serviceId;
    }
}
