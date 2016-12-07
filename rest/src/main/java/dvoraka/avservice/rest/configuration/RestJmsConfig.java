package dvoraka.avservice.rest.configuration;

import dvoraka.avservice.rest.RemoteRestStrategy;
import dvoraka.avservice.rest.RestStrategy;
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
    public RestStrategy restStrategy(ServerComponent serverComponent) {
        return new RemoteRestStrategy(serverComponent);
    }
}
