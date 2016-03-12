package dvoraka.avservice.configuration;

import dvoraka.avservice.*;
import dvoraka.avservice.aop.SpringAopTest;
import dvoraka.avservice.server.AVServer;
import dvoraka.avservice.server.AmqpAVServer;
import dvoraka.avservice.server.ListeningStrategy;
import dvoraka.avservice.server.SimpleAmqpListeningStrategy;
import dvoraka.avservice.service.AVService;
import dvoraka.avservice.service.DefaultAVService;
import dvoraka.avservice.service.DefaultRestService;
import dvoraka.avservice.service.RestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * App Spring configuration.
 */
@Configuration
//@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import({AmqpConfig.class, SpringWebConfig.class})
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
        return new AmqpAVServer(ReceivingType.LISTENER);
    }

    @Bean
    public MessageProcessor messageProcessor() {
        return new DefaultMessageProcessor(2);
    }

    @Bean
    public ListeningStrategy listeningStrategy() {
        return new SimpleAmqpListeningStrategy();
    }

    @Bean
    public SpringAopTest springAopTest() {
        return new SpringAopTest();
    }

    @Bean
    public RestService restService() {
        return new DefaultRestService();
    }

    @Bean
    public RestStrategy restStrategy() {
        return new DirectRestStrategy();
    }
}
