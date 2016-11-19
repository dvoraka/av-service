package dvoraka.avservice.server.checker

import dvoraka.avservice.common.exception.MessageNotFoundException
import dvoraka.avservice.common.testing.DefaultPerformanceTestProperties
import dvoraka.avservice.common.testing.PerformanceTestProperties
import spock.lang.Specification
import spock.lang.Subject

/**
 * Default load tester spec.
 */
class DefaultLoadTesterSpec extends Specification {

    @Subject
    DefaultLoadTester tester

    PerformanceTestProperties props


    def setup() {
        Checker checker = Stub()
        props = new DefaultPerformanceTestProperties.Builder()
                .msgCount(1)
                .build()

        tester = new DefaultLoadTester(checker, props)
    }

    def "shouldn't be running before start"() {
        expect:
            !tester.isRunning()
    }

    def "shouldn't be started before start"() {
        expect:
            !tester.isStarted()
    }

    def "shouldn't be stopped before start"() {
        expect:
            !tester.isStopped()
    }

    def "after start it should be started and running"() {
        when:
            tester.start()

        then:
            tester.isStarted()
            tester.isRunning()
    }

    def "after restart it should be started and running"() {
        when:
            tester.restart()

        then:
            !tester.isStopped()
            tester.isStarted()
            tester.isRunning()
    }

    def "after stop it should be stopped"() {
        when:
            tester.stop()

        then:
            tester.isStopped()
    }

    def "test with message receiving problem"() {
        given:
            Checker checker = Stub()
            checker.receiveMessage(_) >> {
                throw new MessageNotFoundException()
            }

            tester = new DefaultLoadTester(checker, props)

        when:
            tester.start()

        then:
            notThrown(Exception)
    }
}
