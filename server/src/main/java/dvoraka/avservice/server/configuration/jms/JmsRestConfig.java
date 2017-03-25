package dvoraka.avservice.server.configuration.jms;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * JMS REST configuration for import.
 */
@Configuration
@Profile("jms-rest")
public class JmsRestConfig {

//    @Value("${avservice.jms.resultDestination:result}")
//    private String resultDestination;
//
//    @Value("${avservice.jms.checkDestination:check}")
//    private String checkDestination;
//
//    @Value("${avservice.serviceId:default1}")
//    private String serviceId;
//
//
//    @Bean
//    public ServerComponent serverComponent(
//            JmsTemplate jmsTemplate,
//            MessageInfoService messageInfoService
//    ) {
//        return new JmsComponent(checkDestination, serviceId, jmsTemplate, messageInfoService);
//    }
//
//    @Bean
//    public MessageListener messageListener(ServerComponent serverComponent) {
//        return serverComponent;
//    }
//
//    @Bean
//    public SimpleMessageListenerContainer messageListenerContainer(
//            ActiveMQConnectionFactory activeMQConnectionFactory,
//            MessageListener messageListener) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(activeMQConnectionFactory);
//        container.setDestinationName(resultDestination);
//        container.setMessageListener(messageListener);
//
//        return container;
//    }
}
