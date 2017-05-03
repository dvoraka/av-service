package dvoraka.avservice.user.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ldap.core.LdapTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [UserConfig.class])
@ActiveProfiles(['ldap'])
@DirtiesContext
class LdapUserConfigISpec extends Specification {

    @Autowired
    LdapTemplate ldapTemplate


    def "test"() {
        expect:
            true
    }
}
