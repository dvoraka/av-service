package dvoraka.avservice.server.configuration.jms;

import dvoraka.avservice.common.testing.DefaultPerformanceTestProperties;
import dvoraka.avservice.common.testing.PerformanceTestProperties;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.server.ServerComponent;
import dvoraka.avservice.server.checker.CheckApp;
import dvoraka.avservice.server.checker.Checker;
import dvoraka.avservice.server.checker.PerformanceTester;
import dvoraka.avservice.server.checker.SimpleChecker;
import dvoraka.avservice.server.jms.JmsComponent;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import javax.jms.MessageListener;

/**
 * JMS checker configuration for import.
 */
@Configuration
@Profile("jms-checker")
public class JmsCheckerConfig {

    @Value("${avservice.jms.checkDestination:check}")
    private String checkDestination;

    @Value("${avservice.jms.resultDestination:result}")
    private String resultDestination;

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

    @Bean
    public Checker checker(ServerComponent serverComponent) {
        return new SimpleChecker(serverComponent);
    }

    @Bean
    public PerformanceTestProperties testProperties() {
        return new DefaultPerformanceTestProperties();
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
