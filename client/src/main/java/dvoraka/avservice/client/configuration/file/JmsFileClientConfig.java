package dvoraka.avservice.client.configuration.file;

import dvoraka.avservice.client.jms.JmsAdapter;
import dvoraka.avservice.client.transport.AvNetworkComponent;
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
    public AvNetworkComponent avNetworkComponent(
            JmsTemplate jmsTemplate,
            MessageInfoService messageInfoService
    ) {
        return new JmsAdapter(fileDestination, serviceId, jmsTemplate, messageInfoService);
    }

    @Bean
    public MessageListener messageListener(AvNetworkComponent avNetworkComponent) {
        return avNetworkComponent;
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
