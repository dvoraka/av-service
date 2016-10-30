package dvoraka.avservice.avprogram.configuration;

import dvoraka.avservice.avprogram.AvProgram;
import dvoraka.avservice.avprogram.ClamAvProgram;
import dvoraka.avservice.common.service.CachingService;
import dvoraka.avservice.common.service.DefaultCachingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * AV program configuration.
 */
@Configuration
@Profile("core")
public class AvProgramConfig {

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
}
