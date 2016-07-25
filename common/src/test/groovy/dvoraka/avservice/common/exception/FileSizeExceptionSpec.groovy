package dvoraka.avservice.common.exception

import spock.lang.Specification

/**
 * FileSizeException spec.
 */
class FileSizeExceptionSpec extends Specification {

    def "with message"() {
        given:
            String message = "TEST"
            FileSizeException exception = new FileSizeException(message)

        when:
            throw exception

        then:
            def e = thrown(FileSizeException)
            e.getMessage() == message
    }
}
