package dvoraka.avservice.common.exception

import spock.lang.Specification

/**
 * ScanErrorException spec.
 */
class ScanErrorExceptionSpec extends Specification {

    def "with message"() {
        given:
            String message = "TEST"
            ScanException exception = new ScanException(message)

        when:
            throw exception

        then:
            def e = thrown(ScanException)
            e.getMessage() == message
    }

    def "with message and nested exception"() {
        given:
            String message = "TEST"
            IOException nestedException = new IOException()
            ScanException exception = new ScanException(message, nestedException)

        when:
            throw exception

        then:
            def e = thrown(ScanException)
            e.getMessage() == message
            e.getCause() == nestedException
    }
}
