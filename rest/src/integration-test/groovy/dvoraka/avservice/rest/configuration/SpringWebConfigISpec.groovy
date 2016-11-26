package dvoraka.avservice.rest.configuration

import dvoraka.avservice.common.SpringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Configuration spec.
 */
@ContextConfiguration(classes = SpringWebConfig.class)
@ActiveProfiles(['core', 'rest', 'db'])
@WebAppConfiguration
class SpringWebConfigISpec extends Specification {

    @Autowired
    ApplicationContext context
    @Autowired
    UserDetailsService service


    def "test"() {
        expect:
            true
    }

    @Ignore("manual testing")
    def "show context"() {
        expect:
            SpringUtils.printBeansList(context)
    }
}
