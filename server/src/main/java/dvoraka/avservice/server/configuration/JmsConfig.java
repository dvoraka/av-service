package dvoraka.avservice.server.configuration;

import dvoraka.avservice.configuration.ServiceConfig;
import dvoraka.avservice.server.JmsClient;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

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

    @Value("${avservice.jms.receiveTimeout:2000}")
    private long receiveTimeout;


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
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setReceiveTimeout(receiveTimeout);
        template.setMessageConverter(messageConverter());

        return template;
    }

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("typeId");

        return messageConverter;
    }

    @Bean
    public JmsClient jmsClient() {
        return new JmsClient();
    }
}
