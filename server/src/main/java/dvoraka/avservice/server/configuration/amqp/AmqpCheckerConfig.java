package dvoraka.avservice.server.configuration.amqp;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.client.checker.CheckApp;
import dvoraka.avservice.client.checker.Checker;
import dvoraka.avservice.client.checker.PerformanceTester;
import dvoraka.avservice.client.checker.SimpleChecker;
import dvoraka.avservice.common.testing.PerformanceTestProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * AMQP checker configuration for import.
 */
@Configuration
@Profile("amqp-checker")
public class AmqpCheckerConfig {

    @Value("${avservice.amqp.resultQueue}")
    private String resultQueue;
    @Value("${avservice.amqp.checkExchange}")
    private String checkExchange;
    @Value("${avservice.serviceId:default1}")
    private String serviceId;


//    @Bean
//    public ServerComponent serverComponent(
//            RabbitTemplate rabbitTemplate,
//            MessageInfoService messageInfoService
//    ) {
//        return new AmqpComponent(checkExchange, serviceId, rabbitTemplate, messageInfoService);
//    }
//
//    @Bean
//    public MessageListener messageListener(ServerComponent serverComponent) {
//        return serverComponent;
//    }

//    @Bean
//    public MessageListenerContainer messageListenerContainer(
//            ConnectionFactory connectionFactory, MessageListener messageListener) {
//        DirectMessageListenerContainer container = new DirectMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(resultQueue);
//        container.setMessageListener(messageListener);
//
//        return container;
//    }

    @Bean
    public Checker checker(ServerComponent serverComponent) {
        return new SimpleChecker(serverComponent);
    }

    @Bean
    public PerformanceTester defaultLoadTester(
            Checker checker,
            PerformanceTestProperties testProperties
    ) {
        return new PerformanceTester(checker, testProperties);
    }

    @Bean
    public CheckApp checkApp(Checker checker) {
        return new CheckApp(checker);
    }
}
