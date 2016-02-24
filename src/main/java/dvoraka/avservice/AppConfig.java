package dvoraka.avservice;

import dvoraka.avservice.aop.SpringAopTest;
import dvoraka.avservice.server.AVServer;
import dvoraka.avservice.server.AmqpAVServer;
import dvoraka.avservice.server.ListeningStrategy;
import dvoraka.avservice.server.ParallelAmqpListeningStrategy;
import dvoraka.avservice.server.SimpleAmqpListeningStrategy;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * App configuration.
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AppConfig {

    @Bean
    public AVService avService() {
        return new DefaultAVService();
    }

    @Bean
    public AVProgram avProgram() {
        return new ClamAVProgram();
    }

    @Bean
    public AVServer avServer() {
        return new AmqpAVServer();
    }

    @Bean
    public MessageProcessor messageProcessor() {
        return new DefaultMessageProcessor(4);
    }

    @Bean
    public ListeningStrategy listeningStrategy() {
        return new SimpleAmqpListeningStrategy();
//        return new ParallelAmqpListeningStrategy();
    }

    //// AMQP beans
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("antivirus");

        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate amqpTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        // wait forever
        template.setReceiveTimeout(-1);
        template.setRoutingKey("test");
        template.setQueue("clamav-check");

        return template;
    }
    //////////////////////////

    @Bean
    public SpringAopTest springAopTest() {
        return new SpringAopTest();
    }
}
