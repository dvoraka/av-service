package dvoraka.avservice.common.exception

import spock.lang.Specification

/**
 * ScanErrorException spec.
 */
class ScanErrorExceptionSpec extends Specification {

    def "with message"() {
        given:
            String message = "TEST"
            ScanErrorException exception = new ScanErrorException(message)

        when:
            throw exception

        then:
            def e = thrown(ScanErrorException)
            e.getMessage() == message
    }

    def "with message and nested exception"() {
        given:
            String message = "TEST"
            IOException nestedException = new IOException()
            ScanErrorException exception = new ScanErrorException(message, nestedException)

        when:
            throw exception

        then:
            def e = thrown(ScanErrorException)
            e.getMessage() == message
            e.getCause() == nestedException
    }
}
