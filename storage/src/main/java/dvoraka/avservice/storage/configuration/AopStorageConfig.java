package dvoraka.avservice.storage.configuration;

import dvoraka.avservice.storage.FileServiceAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

/**
 * AOP configuration
 */
@Configuration
@EnableAspectJAutoProxy
public class AopStorageConfig {

    @Bean
    @Profile("storage-check")
    public FileServiceAspect fileServiceAspect() {
        return new FileServiceAspect();
    }
}
