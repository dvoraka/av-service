package dvoraka.avservice.common.exception

import spock.lang.Specification

/**
 * Exception spec.
 */
class BadExchangeExceptionSpec extends Specification {

    def "constructor (message)"() {
        given:
            String msg = "Exception message"

        when:
            throw new BadExchangeException(msg)

        then:
            def e = thrown(BadExchangeException)
            msg == e.getMessage()
    }
}
