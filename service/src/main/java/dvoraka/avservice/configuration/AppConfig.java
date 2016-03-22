package dvoraka.avservice.configuration;

import dvoraka.avservice.avprogram.AVProgram;
import dvoraka.avservice.avprogram.ClamAVProgram;
import dvoraka.avservice.DefaultMessageProcessor;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.aop.SpringAopTest;
import dvoraka.avservice.rest.RestClient;
import dvoraka.avservice.server.ReceivingType;
import dvoraka.avservice.service.AVService;
import dvoraka.avservice.service.DefaultAVService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestTemplate;

/**
 * App Spring configuration.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
//@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import({AmqpConfig.class, SpringWebConfig.class})
public class AppConfig {

    @Value("${avservice.rest.url}")
    String restUrl;


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
        return new DefaultMessageProcessor(20, ReceivingType.LISTENER, 0);
    }

    @Bean
    public MessageProcessor restMessageProcessor() {
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

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestClient restClient() {
        return new RestClient(restUrl);
    }
}
