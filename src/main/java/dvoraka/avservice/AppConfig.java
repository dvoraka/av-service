package dvoraka.avservice;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * App configuration.
 */
@Configuration
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
        return null;
    }

    @Bean
    public ListeningStrategy listeningStrategy() {
        return new SimpleAmqpListeningStrategy();
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
        template.setRoutingKey("test");
        template.setQueue("clamav-check");

        return template;
    }
    //////////////////////////
}
