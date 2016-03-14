package dvoraka.avservice.configuration;

import dvoraka.avservice.AVProgram;
import dvoraka.avservice.ClamAVProgram;
import dvoraka.avservice.DefaultMessageProcessor;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.aop.SpringAopTest;
import dvoraka.avservice.service.AVService;
import dvoraka.avservice.service.DefaultAVService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public MessageProcessor messageProcessor() {
        return new DefaultMessageProcessor(2);
    }

    @Bean
    public SpringAopTest springAopTest() {
        return new SpringAopTest();
    }
}
