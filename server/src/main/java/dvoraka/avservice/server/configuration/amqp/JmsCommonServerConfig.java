package dvoraka.avservice.server.configuration.amqp;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.connection.CachingConnectionFactory;

import javax.jms.ConnectionFactory;

/**
 * AMQP common server configuration.
 */
@Configuration
@Profile("jms")
public class JmsCommonServerConfig {

    @Value("${avservice.jms.brokerUrl}")
    private String brokerUrl;


    @Bean
    public ActiveMQConnectionFactory serverActiveMQConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        factory.setUseAsyncSend(true);

        return factory;
    }

    @Bean
    public ConnectionFactory serverConnectionFactory(
            ConnectionFactory serverActiveMQConnectionFactory
    ) {
        return new CachingConnectionFactory(serverActiveMQConnectionFactory);
    }
}
