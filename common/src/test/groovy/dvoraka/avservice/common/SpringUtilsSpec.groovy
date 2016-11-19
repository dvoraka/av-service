package dvoraka.avservice.common

import org.springframework.context.ApplicationContext
import spock.lang.Specification
import spock.lang.Subject

/**
 * Spring utils spec.
 */
class SpringUtilsSpec extends Specification {

    @Subject
    SpringUtils springUtils


    def "print beans list"() {
        given:
            ApplicationContext context = Mock()
            context.getBeanDefinitionNames() >> []

        when:
            SpringUtils.printBeansList(context)

        then:
            notThrown(Exception)

        when:
            SpringUtils.printBeansList(null)

        then:
            thrown(NullPointerException)
    }
}
