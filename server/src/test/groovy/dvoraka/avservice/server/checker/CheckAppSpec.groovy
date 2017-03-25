package dvoraka.avservice.server.checker

import dvoraka.avservice.client.checker.CheckApp
import dvoraka.avservice.client.checker.Checker
import spock.lang.Specification
import spock.lang.Subject

/**
 * Check app spec.
 */
class CheckAppSpec extends Specification {

    @Subject
    CheckApp checkApp

    Checker checker


    def setup() {
        checker = Mock()
        checkApp = new CheckApp(checker)
    }

    def "start with OK status"() {
        when:
            checkApp.start()

        then:
            1 * checker.check() >> true
            !checkApp.isRunning()
    }

    def "start with bad status"() {
        when:
            checkApp.start()

        then:
            1 * checker.check() >> false
            !checkApp.isRunning()
    }
}
