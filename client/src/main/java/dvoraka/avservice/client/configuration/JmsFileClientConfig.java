package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.NetworkComponent;
import dvoraka.avservice.client.jms.JmsComponent;
import dvoraka.avservice.db.service.MessageInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;

/**
 * JMS file client configuration for the import.
 */
@Configuration
@Profile("jms")
public class JmsFileClientConfig {

    @Value("${avservice.jms.fileDestination}")
    private String fileDestination;
    @Value("${avservice.jms.resultDestination}")
    private String resultDestination;

    @Value("${avservice.serviceId}")
    private String serviceId;


    @Bean
    public NetworkComponent serverComponent(
            JmsTemplate jmsTemplate,
            MessageInfoService messageInfoService
    ) {
        return new JmsComponent(fileDestination, serviceId, jmsTemplate, messageInfoService);
    }

    @Bean
    public MessageListener messageListener(NetworkComponent networkComponent) {
        return networkComponent;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(
            ConnectionFactory connectionFactory,
            MessageListener messageListener
    ) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setDestinationName(resultDestination);
        container.setMessageListener(messageListener);

        return container;
    }
}
