package dvoraka.avservice.checker.utils

import dvoraka.avservice.checker.configuration.AmqpCheckerConfig
import dvoraka.avservice.checker.sender.AvSender
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
    @Autowired
    AvSender avSender


    def cleanup() {
        avSender.purgeQueue("av-check")
    }

    def "run negotiate protocol method when server is down"() {
        expect:
            avUtils.negotiateProtocol(["1.0"] as String[]) == null
    }
}
