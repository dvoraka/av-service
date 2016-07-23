package dvoraka.avservice.checker.exception

import spock.lang.Specification

/**
 * Exception spec.
 */
class ProtocolExceptionSpec extends Specification {

    def "constructor"() {
        when:
            throw new ProtocolException()

        then:
            thrown(ProtocolException)
    }

    def "constructor (message)"() {
        given:
            String msg = "Exception message"

        when:
            throw new ProtocolException(msg)

        then:
            def e = thrown(ProtocolException)
            msg.equals(e.getMessage())
    }
}
