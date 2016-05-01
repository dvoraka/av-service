package dvoraka.avservice.checker

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Run checker App.
 */
@ContextConfiguration(classes = [AppConfig])
class CheckerAppISpec extends Specification {

    @Autowired
    AVChecker checker


    def "AV checker loading"() {
        expect:
        checker != null
    }

    def "run checker"() {
        when:
        checker.check()

        then:
        notThrown(Exception)
    }
}
