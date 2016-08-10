package dvoraka.avservice.configuration;

import dvoraka.avservice.CachingService;
import dvoraka.avservice.DefaultCachingService;
import dvoraka.avservice.aop.SpringAopTest;
import dvoraka.avservice.avprogram.AvProgram;
import dvoraka.avservice.avprogram.ClamAvProgram;
import dvoraka.avservice.rest.RestClient;
import dvoraka.avservice.service.AvService;
import dvoraka.avservice.service.DefaultAvService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.web.client.RestTemplate;

/**
 * App Spring configuration.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
//@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableMBeanExport
@Import({SpringWebConfig.class})
public class ServiceConfig {

    @Value("${avservice.rest.url}")
    private String restUrl;


    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public AvService avService(AvProgram avProgram) {
        return new DefaultAvService(avProgram);
    }

    @Bean
    public AvProgram avProgram() {
        AvProgram avProgram = new ClamAvProgram();
        avProgram.setCaching(false);

        return avProgram;
    }

    @Lazy
    @Bean
    public CachingService cachingService() {
        return new DefaultCachingService();
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

    @Bean
    public MBeanExporter mbeanExporter() {
        MBeanExporter exporter = new AnnotationMBeanExporter();
        exporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);

        return exporter;
    }
}
