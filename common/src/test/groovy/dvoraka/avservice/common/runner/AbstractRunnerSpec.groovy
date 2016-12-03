package dvoraka.avservice.common.runner

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Specification
import spock.lang.Subject

/**
 * AbstractRunner spec.
 */
class AbstractRunnerSpec extends Specification {

    @Subject
    AbstractRunner runner


    def setup() {
        runner = Spy()
    }

    def "applicationContext"() {
        given:
            String[] profiles = ['test1', 'test2']
            runner.profiles() >> profiles

            Class<?>[] classes = [AbstractRunnerSpec.class]
            runner.configClasses() >> classes

        when:
            AnnotationConfigApplicationContext context = runner.applicationContext()

        then:
            Arrays.equals(context.getEnvironment().getActiveProfiles(), profiles)
    }

    def "default profiles"() {
        expect:
            Arrays.equals(runner.profiles(), ['default'] as String[])

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

    def "testing wait for key method"() {
        given:
            AbstractRunner.setTestRun(true)

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
            AbstractRunner.setTestRun(true)

        then:
            AbstractRunner.isTestRun()
    }
}
