package dvoraka.avservice.rest.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification
import spock.lang.Subject

/**
 * BasicUserDetailsService spec.
 */
class BasicUserDetailsServiceSpec extends Specification {

    @Subject
    BasicUserDetailsService service


    def setup() {
        service = new BasicUserDetailsService()
    }

    def "LoadUserByUsername with right username"() {
        when:
            UserDetails userDetails = service.loadUserByUsername("JOHN")

        then:
            userDetails
    }

    def "LoadUserByUsername with non-existent username"() {
        when:
            service.loadUserByUsername("xxx")

        then:
            thrown(UsernameNotFoundException)
    }
}
