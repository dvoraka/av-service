package dvoraka.avservice.storage.configuration;

import dvoraka.avservice.storage.FileServiceAspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * AOP configuration
 */
@Configuration
@EnableAspectJAutoProxy
@Import({
        FileServiceAspect.class
})
public class AopConfiguration {
}
