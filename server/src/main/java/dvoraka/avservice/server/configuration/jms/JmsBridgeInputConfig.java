package dvoraka.avservice.server.configuration.jms;

import dvoraka.avservice.server.ServerComponent;
import dvoraka.avservice.server.jms.JmsComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;

/**
 * JMS bridge input configuration for import.
 */
@Configuration
@Profile("jms-bridge-input")
public class JmsBridgeInputConfig {

    @Value("${avservice.jms.checkDestination:check}")
    private String checkDestination;
    @Value("${avservice.jms.resultDestination:result}")
    private String resultDestination;

    @Value("${avservice.serviceId:default1")
    private String serviceId;


    @Bean
    public ServerComponent inComponent() {
        return new JmsComponent(resultDestination, serviceId);
    }

    @Bean
    public SimpleMessageListenerContainer inMessageListenerContainer(
            ConnectionFactory activeMQConnFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(activeMQConnFactory);
        container.setDestinationName(checkDestination);
        container.setMessageListener(inMessageListener());

        return container;
    }

    @Bean
    public MessageListener inMessageListener() {
        return inComponent();
    }
}
