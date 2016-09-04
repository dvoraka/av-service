package dvoraka.avservice.rest.service

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification

/**
 * BasicUserDetailsService spec.
 */
class BasicUserDetailsServiceSpec extends Specification {

    BasicUserDetailsService service


    def setup() {
        service = new BasicUserDetailsService()
    }

    def "LoadUserByUsername with right username"() {
        when:
            User user = service.loadUserByUsername("JOHN")

        then:
            user
    }

    def "LoadUserByUsername with non-existent username"() {
        when:
            service.loadUserByUsername("xxx")

        then:
            thrown(UsernameNotFoundException)
    }
}
