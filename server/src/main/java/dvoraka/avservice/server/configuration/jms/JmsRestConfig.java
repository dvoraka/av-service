package dvoraka.avservice.server.configuration.jms;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.client.jms.JmsComponent;
import dvoraka.avservice.db.service.MessageInfoService;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

/**
 * JMS REST configuration for import.
 */
@Configuration
@Profile("jms-rest")
public class JmsRestConfig {

    @Value("${avservice.jms.resultDestination:result}")
    private String resultDestination;

    @Value("${avservice.jms.checkDestination:check}")
    private String checkDestination;

    @Value("${avservice.serviceId:default1}")
    private String serviceId;


    @Bean
    public ServerComponent serverComponent(
            JmsTemplate jmsTemplate,
            MessageInfoService messageInfoService
    ) {
        return new JmsComponent(checkDestination, serviceId, jmsTemplate, messageInfoService);
    }

    @Bean
    public MessageListener messageListener(ServerComponent serverComponent) {
        return serverComponent;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(
            ActiveMQConnectionFactory activeMQConnectionFactory,
            MessageListener messageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(activeMQConnectionFactory);
        container.setDestinationName(resultDestination);
        container.setMessageListener(messageListener);

        return container;
    }
}
