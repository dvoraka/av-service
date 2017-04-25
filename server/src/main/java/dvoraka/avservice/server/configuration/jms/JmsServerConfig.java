package dvoraka.avservice.server.configuration.jms;

import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.client.jms.JmsComponent;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.server.AvServer;
import dvoraka.avservice.server.BasicAvServer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;

/**
 * JMS server configuration for import.
 */
@Configuration
@Profile("jms")
public class JmsServerConfig {

    @Value("${avservice.jms.brokerUrl}")
    private String brokerUrl;
    @Value("${avservice.jms.receiveTimeout:2000}")
    private long receiveTimeout;

    @Value("${avservice.jms.resultDestination:result}")
    private String resultDestination;
    @Value("${avservice.jms.checkDestination:check}")
    private String checkDestination;

    @Value("${avservice.serviceId:default1}")
    private String serviceId;


    @Bean
    public AvServer avServer(
            ServerComponent serverComponent,
            MessageProcessor checkMessageProcessor,
            MessageInfoService messageInfoService
    ) {
        return new BasicAvServer(
                serviceId,
                serverComponent,
                checkMessageProcessor,
                messageInfoService
        );
    }

    @Bean
    public ServerComponent serverComponent(
            JmsTemplate jmsTemplate,
            MessageInfoService messageInfoService
    ) {
        return new JmsComponent(resultDestination, serviceId, jmsTemplate, messageInfoService);
    }

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

    @Bean
    public MessageListener messageListener(ServerComponent serverComponent) {
        return serverComponent;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(
            ActiveMQConnectionFactory serverActiveMQConnectionFactory,
            MessageListener messageListener
    ) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(serverActiveMQConnectionFactory);
        container.setDestinationName(checkDestination);
        container.setMessageListener(messageListener);

        return container;
    }
}
