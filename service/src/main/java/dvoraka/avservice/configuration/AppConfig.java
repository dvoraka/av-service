package dvoraka.avservice.configuration;

import dvoraka.avservice.aop.SpringAopTest;
import dvoraka.avservice.avprogram.AVProgram;
import dvoraka.avservice.avprogram.ClamAVProgram;
import dvoraka.avservice.rest.RestClient;
import dvoraka.avservice.service.AVService;
import dvoraka.avservice.service.DefaultAVService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
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
@EnableMBeanExport
@Import({AmqpConfig.class, SpringWebConfig.class})
public class AppConfig {

    @Value("${avservice.rest.url}")
    private String restUrl;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public AVService avService() {
        return new DefaultAVService();
    }

    @Bean
    public AVProgram avProgram() {
        return new ClamAVProgram();
    }

    @Bean
    public SpringAopTest springAopTest() {
        return new SpringAopTest();
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
