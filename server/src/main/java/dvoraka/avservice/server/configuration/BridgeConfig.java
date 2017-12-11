package dvoraka.avservice.server.configuration;

import dvoraka.avservice.client.AvNetworkComponent;
import dvoraka.avservice.db.configuration.DatabaseConfig;
import dvoraka.avservice.server.AvNetworkComponentBridge;
import dvoraka.avservice.server.configuration.amqp.AmqpBridgeOutputConfig;
import dvoraka.avservice.server.configuration.amqp.AmqpCommonServerConfig;
import dvoraka.avservice.server.configuration.amqp.AmqpServerConfig;
import dvoraka.avservice.server.configuration.jms.JmsBridgeOutputConfig;
import dvoraka.avservice.server.configuration.jms.JmsCommonServerConfig;
import dvoraka.avservice.server.configuration.jms.JmsServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Bridge main configuration.
 *
 * @see ServerConfig
 */
@Configuration
@PropertySource("classpath:avservice.properties")
@Profile("bridge")
@Import({
        // AMQP to JMS
        AmqpServerConfig.class,
        AmqpCommonServerConfig.class,
        JmsBridgeOutputConfig.class,
        // JMS to AMQP
        JmsServerConfig.class,
        JmsCommonServerConfig.class,
        AmqpBridgeOutputConfig.class,

        DatabaseConfig.class
})
public class BridgeConfig {

    @Bean
    public AvNetworkComponentBridge componentBridge(
            AvNetworkComponent fileAvNetworkComponent,
            AvNetworkComponent outComponent
    ) {
        return new AvNetworkComponentBridge(fileAvNetworkComponent, outComponent);
    }
}
