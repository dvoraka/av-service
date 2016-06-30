package dvoraka.avservice.checker.utils

import dvoraka.avservice.checker.configuration.CheckerConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * AV utils test.
 */
@ContextConfiguration(classes = [CheckerConfig])
class AvUtilsISpec extends Specification {

    @Autowired
    AvUtils avUtils


    // TODO: implement properly
    def "run negotiate protocol method"() {
        expect:
        avUtils.negotiateProtocol(["1.0"] as String[]) == null
    }
}
