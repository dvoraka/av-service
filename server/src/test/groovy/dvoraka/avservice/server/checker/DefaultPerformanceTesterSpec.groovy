package dvoraka.avservice.server.checker

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
    DefaultPerformanceTester tester

    PerformanceTestProperties props


    def setup() {
        Checker checker = Stub()
        props = new DefaultPerformanceTestProperties.Builder()
                .msgCount(1)
                .build()

        tester = new DefaultPerformanceTester(checker, props)
    }

    def "shouldn't be running before start"() {
        expect:
            !tester.isRunning()
    }

    def "after start it should be started and running"() {
        when:
            tester.start()

        then:
            tester.isRunning()
    }

    def "test with message receiving problem"() {
        given:
            Checker checker = Stub()
            checker.receiveMessage(_) >> {
                throw new MessageNotFoundException()
            }

            tester = new DefaultPerformanceTester(checker, props)

        when:
            tester.start()

        then:
            notThrown(Exception)
    }
}
