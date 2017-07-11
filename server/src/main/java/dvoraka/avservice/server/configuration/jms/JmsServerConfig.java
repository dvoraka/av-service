package dvoraka.avservice.server.configuration.jms;

import dvoraka.avservice.client.NetworkComponent;
import dvoraka.avservice.client.jms.JmsComponent;
import dvoraka.avservice.db.service.MessageInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;

/**
 * JMS server configuration for the import.
 */
@Configuration
@Profile("jms")
public class JmsServerConfig {

    @Value("${avservice.jms.receiveTimeout:2000}")
    private long receiveTimeout;

    @Value("${avservice.jms.fileDestination}")
    private String fileDestination;
    @Value("${avservice.jms.resultDestination}")
    private String resultDestination;

    @Value("${avservice.serviceId:default1}")
    private String serviceId;


    @Bean
    public NetworkComponent fileNetworkComponent(
            JmsTemplate fileServerJmsTemplate,
            MessageInfoService messageInfoService
    ) {
        return new JmsComponent(
                resultDestination, serviceId, fileServerJmsTemplate, messageInfoService);
    }

    @Bean
    public MessageConverter fileServerMessageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("typeId");

        return messageConverter;
    }

    @Bean
    public JmsTemplate fileServerJmsTemplate(
            ConnectionFactory serverConnectionFactory,
            MessageConverter fileServerMessageConverter
    ) {
        JmsTemplate template = new JmsTemplate(serverConnectionFactory);
        template.setReceiveTimeout(receiveTimeout);
        template.setMessageConverter(fileServerMessageConverter);

        return template;
    }

    @Bean
    public MessageListener fileServerMessageListener(NetworkComponent fileNetworkComponent) {
        return fileNetworkComponent;
    }

    @Bean
    public SimpleMessageListenerContainer fileServerMessageListenerContainer(
            ConnectionFactory serverConnectionFactory,
            MessageListener fileServerMessageListener
    ) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(serverConnectionFactory);
        container.setDestinationName(fileDestination);
        container.setMessageListener(fileServerMessageListener);

        return container;
    }
}
