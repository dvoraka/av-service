package dvoraka.avservice.common.runner

import dvoraka.avservice.common.service.ServiceManagement
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Specification
import spock.lang.Subject

/**
 * AbstractRunner spec.
 */
class AbstractServiceRunnerSpec extends Specification {

    @Subject
    AbstractServiceRunner runner


    def setup() {
        runner = Spy()
    }

    def "default stop message"() {
        expect:
            runner.stopMessage() == 'Press Enter to stop.'
    }

    def "runner status before start"() {
        expect:
            !runner.isRunning()
            !runner.isStopped()
    }

    def "run"() {
        given:
            AnnotationConfigApplicationContext context = Mock()
            context.getBean(_) >> Mock(ServiceManagement)

            runner.applicationContext() >> context
            runner.runClass() >> null
            runner.setTestRun(true)

        when:
            runner.run()

        then:
            notThrown(Exception)
            runner.isStopped()
            !runner.isRunning()
    }

    def "testing wait for key method"() {
        given:
            AbstractServiceRunner.setTestRun(true)

        when:
            runner.waitForKey()

        then:
            notThrown(Exception)
    }

    def "runner status after stop"() {
        when:
            runner.stop()

        then:
            runner.isStopped()
    }

    def "set running status"() {
        when:
            runner.setRunning(false)

        then:
            !runner.isRunning()

        when:
            runner.setRunning(true)

        then:
            runner.isRunning()
    }

    def "set test run"() {
        when:
            AbstractServiceRunner.setTestRun(true)

        then:
            AbstractServiceRunner.isTestRun()
    }
}
