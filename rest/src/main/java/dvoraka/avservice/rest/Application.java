package dvoraka.avservice.rest;

import dvoraka.avservice.rest.configuration.SpringWebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring Boot application for REST.
 */
@EnableAutoConfiguration
@Configuration
@Import(SpringWebConfig.class)
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class Application { //NOSONAR

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
