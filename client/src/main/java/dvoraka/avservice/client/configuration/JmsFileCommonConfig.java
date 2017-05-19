package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.QueueCleaner;
import dvoraka.avservice.client.jms.JmsQueueCleaner;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.ConnectionFactory;

/**
 * JMS client common configuration for the import.
 */
@Configuration
@Profile("jms")
public class JmsFileCommonConfig {

    @Value("${avservice.jms.brokerUrl}")
    private String brokerUrl;
    @Value("${avservice.jms.receiveTimeout:2000}")
    private long receiveTimeout;


    @Bean
    public ActiveMQConnectionFactory activeMQConnFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        factory.setUseAsyncSend(true);

        return factory;
    }

    @Bean
    public ConnectionFactory connectionFactory(ConnectionFactory activeMQConnFactory) {
        return new CachingConnectionFactory(activeMQConnFactory);
    }

    @Bean
    public JmsTemplate jmsTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setReceiveTimeout(receiveTimeout);
        template.setMessageConverter(messageConverter);

        return template;
    }

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("typeId");

        return messageConverter;
    }

    @Bean
    public QueueCleaner queueCleaner(JmsTemplate jmsTemplate) {
        return new JmsQueueCleaner(jmsTemplate);
    }
}
