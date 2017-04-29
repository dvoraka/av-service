package dvoraka.avservice.server.configuration;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.db.configuration.DatabaseConfig;
import dvoraka.avservice.server.ServerComponentBridge;
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
    public ServerComponentBridge componentBridge(
            ServerComponent fileServerComponent,
            ServerComponent outComponent
    ) {
        return new ServerComponentBridge(fileServerComponent, outComponent);
    }
}