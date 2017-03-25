package dvoraka.avservice.server.configuration.amqp;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * AMQP REST configuration for import.
 */
@Configuration
@Profile("amqp-rest")
public class AmqpRestConfig {

//    @Value("${avservice.amqp.resultQueue}")
//    private String resultQueue;
//    @Value("${avservice.amqp.checkExchange}")
//    private String checkExchange;
//    @Value("${avservice.amqp.fileExchange}")
//    private String fileExchange;
//
//    @Value("${avservice.serviceId:default1}")
//    private String serviceId;
//
//
//    @Bean
//    public ServerComponent serverComponent(
//            RabbitTemplate rabbitTemplate,
//            MessageInfoService messageInfoService
//    ) {
//        return new AmqpComponent(fileExchange, serviceId, rabbitTemplate, messageInfoService);
//    }
//
//    @Bean
//    public MessageListener messageListener(ServerComponent serverComponent) {
//        return serverComponent;
//    }
//
//    @Bean
//    public SimpleMessageListenerContainer messageListenerContainer(
//            ConnectionFactory connectionFactory, MessageListener messageListener) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(resultQueue);
//        container.setMessageListener(messageListener);
//
//        return container;
//    }
}
