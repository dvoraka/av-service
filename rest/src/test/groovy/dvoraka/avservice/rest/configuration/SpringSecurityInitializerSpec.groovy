package dvoraka.avservice.rest.configuration

import spock.lang.Specification
import spock.lang.Subject

/**
 * Initializer spec.
 */
class SpringSecurityInitializerSpec extends Specification {

    @Subject
    SpringSecurityInitializer initializer


    def "constructor"() {
        when:
            initializer = new SpringSecurityInitializer()

        then:
            notThrown(Exception)
    }
}
