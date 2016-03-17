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
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * App Spring configuration.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
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
        return new DefaultMessageProcessor(20);
    }

    @Bean
    public SpringAopTest springAopTest() {
        return new SpringAopTest();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
