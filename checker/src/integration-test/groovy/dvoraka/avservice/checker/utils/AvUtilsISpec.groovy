package dvoraka.avservice.checker.utils

import dvoraka.avservice.checker.configuration.AmqpCheckerConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * AV utils test.
 */
@ContextConfiguration(classes = [AmqpCheckerConfig.class])
class AvUtilsISpec extends Specification {

    @Autowired
    AvUtils avUtils


    def "run negotiate protocol method when server is down"() {
        expect:
            avUtils.negotiateProtocol(["1.0"] as String[]) == null
    }
}
