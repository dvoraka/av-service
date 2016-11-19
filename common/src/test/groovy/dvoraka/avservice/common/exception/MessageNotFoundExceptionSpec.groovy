package dvoraka.avservice.common.exception

import spock.lang.Specification

/**
 * Exception spec.
 */
class MessageNotFoundExceptionSpec extends Specification {

    def "constructor"() {
        when:
            throw new MessageNotFoundException()

        then:
            thrown(MessageNotFoundException)
    }
}
