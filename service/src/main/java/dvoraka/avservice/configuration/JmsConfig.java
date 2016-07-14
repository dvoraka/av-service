package dvoraka.avservice.configuration;

import dvoraka.avservice.JmsClient;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

/**
 * JMS Spring configuration.
 */
@Configuration
@Import({ServiceConfig.class})
@Profile("jms")
public class JmsConfig {

    @Value("${avservice.jms.brokerUrl}")
    private String brokerUrl;


    @Bean
    public ActiveMQConnectionFactory activeMQConnFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);

        return factory;
    }

    @Bean
    public ConnectionFactory connectionFactory(ConnectionFactory activeMQConnFactory) {
        ConnectionFactory factory = new CachingConnectionFactory(activeMQConnFactory);

        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        return new JmsTemplate(connectionFactory);
    }

    @Bean
    public JmsClient jmsClient() {
        return new JmsClient();
    }
}
