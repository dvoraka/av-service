package dvoraka.avservice.rest

import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class GetPassHashSpec extends Specification {

    def "print hash"() {
        expect:
            PasswordEncoder encoder = PasswordEncoderFactories
                    .createDelegatingPasswordEncoder();
            System.out.println(encoder.encode("password"));
    }
}
