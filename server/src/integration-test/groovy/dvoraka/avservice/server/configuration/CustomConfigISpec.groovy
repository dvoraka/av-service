package dvoraka.avservice.server.configuration

import dvoraka.avservice.common.util.SpringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.support.AbstractApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Custom configuration test.
 */
@Ignore("manual testing")
@ContextConfiguration(classes = [ServerConfig.class])
@ActiveProfiles(['jms', 'jms-client'])
class CustomConfigISpec extends Specification {

    @Autowired
    AbstractApplicationContext applicationContext


    def "show context"() {
        setup:
            SpringUtils.printBeansList(applicationContext)

        expect:
            true
    }
}
