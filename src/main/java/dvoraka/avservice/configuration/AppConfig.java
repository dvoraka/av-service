package dvoraka.avservice.configuration;

import dvoraka.avservice.AVProgram;
import dvoraka.avservice.service.AVService;
import dvoraka.avservice.ClamAVProgram;
import dvoraka.avservice.service.DefaultAVService;
import dvoraka.avservice.DefaultMessageProcessor;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.ReceivingType;
import dvoraka.avservice.aop.SpringAopTest;
import dvoraka.avservice.server.AVServer;
import dvoraka.avservice.server.AmqpAVServer;
import dvoraka.avservice.server.ListeningStrategy;
import dvoraka.avservice.server.SimpleAmqpListeningStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * App Spring configuration.
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import(AmqpConfig.class)
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
        return new DefaultMessageProcessor(4);
    }

    @Bean
    public ListeningStrategy listeningStrategy() {
        return new SimpleAmqpListeningStrategy();
//        return new ParallelAmqpListeningStrategy();
    }

    @Bean
    public SpringAopTest springAopTest() {
        return new SpringAopTest();
    }
}
