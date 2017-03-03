package dvoraka.avservice.user.service

import dvoraka.avservice.user.configuration.LdapConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ldap.core.LdapTemplate
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * LDAP configuration.
 */
@ContextConfiguration(classes = [LdapConfig.class])
@Ignore('WIP')
class LdapUserServiceISpec extends Specification {

    @Autowired
    LdapTemplate ldapTemplate


    def "test"() {
        expect:
            true
    }
}
