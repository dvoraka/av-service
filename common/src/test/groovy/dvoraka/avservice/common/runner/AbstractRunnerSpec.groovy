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

            Class<?>[] classes = [AbstractRunnerSpec]
            runner.configClasses() >> classes

        when:
            AnnotationConfigApplicationContext context = runner.applicationContext()

        then:
            Arrays.equals(context.getEnvironment().getActiveProfiles(), profiles)
    }
}
