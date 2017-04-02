package dvoraka.avservice.avprogram.configuration;

import dvoraka.avservice.avprogram.AvProgram;
import dvoraka.avservice.avprogram.ClamAvProgram;
import dvoraka.avservice.avprogram.service.AvService;
import dvoraka.avservice.avprogram.service.DefaultAvService;
import dvoraka.avservice.common.service.CachingService;
import dvoraka.avservice.common.service.DefaultCachingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * AV program configuration.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
@Profile("core")
public class AvProgramConfig {

    @Value("${avservice.avprogram.host}")
    private String host;
    @Value("${avservice.avprogram.port}")
    private int port;
    @Value("${avservice.avprogram.maxScanSize}")
    private int maxArraySize;


    @Bean
    public AvProgram avProgram() {
        AvProgram avProgram = new ClamAvProgram(host, port, maxArraySize, true);
        avProgram.setCaching(false);

        return avProgram;
    }

    @Bean
    public CachingService cachingService() {
        return new DefaultCachingService();
    }

    @Bean
    public AvService avService(AvProgram avProgram) {
        return new DefaultAvService(avProgram);
    }
}
