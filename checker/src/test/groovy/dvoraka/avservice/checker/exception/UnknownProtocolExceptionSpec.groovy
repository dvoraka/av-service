package dvoraka.avservice.checker.exception

import spock.lang.Specification

/**
 * Exception spec.
 */
class UnknownProtocolExceptionSpec extends Specification {

    def "constructor"() {
        when:
        throw new UnknownProtocolException()

        then:
        thrown(UnknownProtocolException)
    }

    def "constructor (message)"() {
        given:
        String msg = "Exception message"

        when:
        throw new UnknownProtocolException(msg)

        then:
        def e = thrown(UnknownProtocolException)
        msg.equals(e.getMessage())
    }
}
