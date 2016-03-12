package dvoraka.avservice.configuration;

import dvoraka.avservice.rest.AVController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Spring REST configuration.
 */
@EnableWebMvc
@Configuration
public class SpringWebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public AVController avController() {
        return new AVController();
    }
}
