package dvoraka.avservice.checker

import dvoraka.avservice.checker.configuration.CheckerConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Run checker App.
 */
@ContextConfiguration(classes = [CheckerConfig])
class CheckerAppISpec extends Specification {

    @Autowired
    AvChecker checker


    def "AvChecker loading"() {
        expect:
        checker != null
    }

    def "check method test"() {
        when:
        checker.check()

        then:
        notThrown(Exception)
    }
}
