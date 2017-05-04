package dvoraka.avservice.user.service

import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification
import spock.lang.Subject

/**
 * Service spec.
 */
class DummyUserServiceSpec extends Specification {

    @Subject
    DummyUserService service


    def setup() {
        service = new DummyUserService()
    }

    def "use service"() {
        when:
            service.loadUserByUsername('some username')

        then:
            thrown(UsernameNotFoundException)
    }
}
