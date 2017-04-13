package dvoraka.avservice.client.checker

import dvoraka.avservice.common.exception.MessageNotFoundException
import dvoraka.avservice.common.testing.DefaultPerformanceTestProperties
import dvoraka.avservice.common.testing.PerformanceTestProperties
import spock.lang.Specification
import spock.lang.Subject

/**
 * Default performance tester spec.
 */
class DefaultPerformanceTesterSpec extends Specification {

    @Subject
    PerformanceTester tester

    PerformanceTestProperties props
    Checker checker


    def setup() {
        checker = Stub()
        props = new DefaultPerformanceTestProperties.Builder()
                .msgCount(1)
                .build()

        tester = new PerformanceTester(checker, props)
    }

    def "shouldn't be running before start"() {
        expect:
            !tester.isRunning()
    }

    def "after start ended it should be stopped"() {
        when:
            tester.start()

        then:
            !tester.isRunning()
    }

    def "test with message receiving problem"() {
        given:
            checker.receiveMessage(_) >> {
                throw new MessageNotFoundException()
            }

        when:
            tester.start()

        then:
            notThrown(Exception)
    }
}
