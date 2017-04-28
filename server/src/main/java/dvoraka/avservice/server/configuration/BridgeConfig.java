package dvoraka.avservice.server.configuration;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.server.ServerComponentBridge;
import dvoraka.avservice.server.configuration.amqp.AmqpServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Bridge main configuration.
 */
@Configuration
@Profile("bridge")
@Import({
        AmqpServerConfig.class,
})
public class BridgeConfig {

    @Bean
    public ServerComponentBridge componentBridge(
            ServerComponent fileServerComponent,
            ServerComponent outComponent
    ) {
        return new ServerComponentBridge(fileServerComponent, outComponent);
    }
}
