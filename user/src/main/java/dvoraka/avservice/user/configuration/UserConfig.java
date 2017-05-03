package dvoraka.avservice.user.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * User module main configuration.
 */
@Configuration
@Import({
        LdapUserConfig.class
})
public class UserConfig {
}
