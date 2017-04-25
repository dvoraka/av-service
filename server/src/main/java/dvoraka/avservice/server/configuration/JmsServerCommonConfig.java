package dvoraka.avservice.server.configuration;

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
 * JMS server common configuration.
 */
@Configuration
@Profile("jms")
public class JmsServerCommonConfig {

    @Value("${avservice.jms.brokerUrl}")
    private String brokerUrl;
    @Value("${avservice.jms.receiveTimeout:2000}")
    private long receiveTimeout;


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

    @Bean
    public MessageConverter serverMessageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("typeId");

        return messageConverter;
    }

    @Bean
    public JmsTemplate fileServerJmsTemplate(
            ConnectionFactory serverConnectionFactory,
            MessageConverter serverMessageConverter
    ) {
        JmsTemplate template = new JmsTemplate(serverConnectionFactory);
        template.setReceiveTimeout(receiveTimeout);
        template.setMessageConverter(serverMessageConverter);

        return template;
    }
}
