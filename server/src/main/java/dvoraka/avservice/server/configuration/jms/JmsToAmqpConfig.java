package dvoraka.avservice.server.configuration.jms;

import dvoraka.avservice.configuration.ServiceConfig;
import dvoraka.avservice.server.ServerComponent;
import dvoraka.avservice.server.ServerComponentBridge;
import dvoraka.avservice.server.configuration.amqp.AmqpBridgeOutputConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * JMS to AMQP bridge Spring configuration.
 */
@Configuration
@Import({
        ServiceConfig.class,
        JmsBridgeInputConfig.class,
        AmqpBridgeOutputConfig.class
})
@Profile("jms2amqp")
public class JmsToAmqpConfig {

    @Bean
    public ServerComponentBridge componentBridge(
            ServerComponent inComponent,
            ServerComponent outComponent) {
        return new ServerComponentBridge(inComponent, outComponent);
    }
}
