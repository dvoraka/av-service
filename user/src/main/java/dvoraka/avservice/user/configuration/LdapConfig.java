package dvoraka.avservice.user.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.test.unboundid.EmbeddedLdapServerFactoryBean;

/**
 * LDAP configuration.
 */
@Configuration
public class LdapConfig {

    @Bean
    public LdapTemplate ldapTemplate(ContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }

    @Bean
    public EmbeddedLdapServerFactoryBean embeddedLdapServerFactoryBean() {
        EmbeddedLdapServerFactoryBean bean = new EmbeddedLdapServerFactoryBean();
        bean.setPartitionName("test1");
        bean.setPartitionSuffix("dc=test,dc=local");
//        bean.setPort();

        return bean;
    }

    @Bean
    public ContextSource contextSource() {
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl("");

        return ldapContextSource;
    }

    // we probably don't need embedded server for testing
}
