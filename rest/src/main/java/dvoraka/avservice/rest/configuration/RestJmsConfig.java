package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.rest.RemoteRestService;
import dvoraka.avservice.rest.service.AvRestService;
import dvoraka.avservice.server.ServerComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * REST JMS configuration.
 */
@Configuration
@Profile("rest-jms")
public class RestJmsConfig {

    @Bean
    public AvRestService avRestService(ServerComponent serverComponent) {
        return new RemoteRestService(serverComponent);
    }
}
