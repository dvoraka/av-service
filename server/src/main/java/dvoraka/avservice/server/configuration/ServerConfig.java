package dvoraka.avservice.server.configuration;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.core.MessageProcessor;
import dvoraka.avservice.core.configuration.CoreConfig;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.server.AvServer;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.configuration.amqp.AmqpCommonServerConfig;
import dvoraka.avservice.server.configuration.amqp.AmqpServerConfig;
import dvoraka.avservice.server.configuration.jms.JmsCommonServerConfig;
import dvoraka.avservice.server.configuration.jms.JmsServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Server module main configuration.
 *
 * @see BridgeConfig
 */
@Configuration
@PropertySource("classpath:avservice.properties")
@Profile("server")
@Import({
        // AMQP
        AmqpServerConfig.class,
        AmqpCommonServerConfig.class,
        // JMS
        JmsServerConfig.class,
        JmsCommonServerConfig.class,

        CoreConfig.class,
})
public class ServerConfig {

    @Value("${avservice.serviceId}")
    private String serviceId;


    @Bean
    public AvServer fileServer(
            ServerComponent fileServerComponent,
            MessageProcessor messageProcessor,
            MessageInfoService messageInfoService
    ) {
        return new BasicAvServer(
                serviceId,
                fileServerComponent,
                messageProcessor,
                messageInfoService
        );
    }
}
