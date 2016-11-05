package dvoraka.avservice.server.configuration.bridge;

import dvoraka.avservice.server.ServerComponent;
import dvoraka.avservice.server.ServerComponentBridge;
import dvoraka.avservice.server.configuration.ServerCommonConfig;
import dvoraka.avservice.server.configuration.amqp.AmqpBridgeOutputConfig;
import dvoraka.avservice.server.configuration.jms.JmsBridgeInputConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * JMS to AMQP bridge Spring configuration.
 */
@Configuration
@Import({
        ServerCommonConfig.class,
        JmsBridgeInputConfig.class,
        AmqpBridgeOutputConfig.class
})
@Profile("jms2amqp")
public class JmsToAmqpConfig {

    @Bean
    public ServerComponentBridge componentBridge(
            ServerComponent inComponent,
            ServerComponent outComponent
    ) {
        return new ServerComponentBridge(inComponent, outComponent);
    }
}
