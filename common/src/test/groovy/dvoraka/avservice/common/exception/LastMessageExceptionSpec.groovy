package dvoraka.avservice.common.exception

import spock.lang.Specification

/**
 * Exception spec.
 */
class LastMessageExceptionSpec extends Specification {

    def "constructor"() {
        when:
            throw new LastMessageException()

        then:
            thrown(LastMessageException)
    }
}
