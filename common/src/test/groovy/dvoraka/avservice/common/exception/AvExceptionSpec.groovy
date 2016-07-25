package dvoraka.avservice.common.exception

import spock.lang.Specification

/**
 * AvException spec.
 */
class AvExceptionSpec extends Specification {

    def "with message"() {
        given:
            String message = "TEST"
            AvException exception = new AvException(message)

        when:
            throw exception

        then:
            def e = thrown(AvException)
            e.getMessage() == message
    }

    def "with message and nested exception"() {
        given:
            String message = "TEST"
            IOException nestedException = new IOException()
            AvException exception = new AvException(message, nestedException)

        when:
            throw exception

        then:
            def e = thrown(AvException)
            e.getMessage() == message
            e.getCause() == nestedException
    }
}
