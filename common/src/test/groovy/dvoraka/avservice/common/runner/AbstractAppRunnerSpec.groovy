package dvoraka.avservice.common.runner

import dvoraka.avservice.common.service.ApplicationManagement
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Specification
import spock.lang.Subject

/**
 * AbstractRunner spec.
 */
class AbstractAppRunnerSpec extends Specification {

    @Subject
    AbstractAppRunner runner


    def setup() {
        runner = Spy()
    }

    def "applicationContext"() {
        given:
            String[] profiles = ['test1', 'test2']
            runner.profiles() >> profiles

            Class<?>[] classes = [AbstractAppRunnerSpec.class]
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

    def "runner status before start"() {
        expect:
            !runner.isRunning()
    }

    def "run"() {
        given:
            AnnotationConfigApplicationContext context = Mock()
            context.getBean(_) >> Mock(ApplicationManagement)

            runner.applicationContext() >> context
            runner.runClass() >> null

        when:
            runner.run()

        then:
            notThrown(Exception)
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
}
