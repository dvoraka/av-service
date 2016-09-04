package dvoraka.avservice.configuration;

import dvoraka.avservice.CachingService;
import dvoraka.avservice.DefaultCachingService;
import dvoraka.avservice.aop.SpringAopTest;
import dvoraka.avservice.avprogram.AvProgram;
import dvoraka.avservice.avprogram.ClamAvProgram;
import dvoraka.avservice.db.configuration.DatabaseConfig;
import dvoraka.avservice.service.AvService;
import dvoraka.avservice.service.DefaultAvService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * App Spring configuration.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
//@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableMBeanExport
@Import({DatabaseConfig.class})
public class ServiceConfig {


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

    @Bean
    public CachingService cachingService() {
        return new DefaultCachingService();
    }

    @Bean
    public SpringAopTest springAopTest() {
        return new SpringAopTest();
    }

//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }
//
//    @Bean
//    public RestClient restClient() {
//        return new RestClient(restUrl);
//    }

    @Bean
    public MBeanExporter mbeanExporter() {
        MBeanExporter exporter = new AnnotationMBeanExporter();
        exporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);

        return exporter;
    }
}
