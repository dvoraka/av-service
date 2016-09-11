package dvoraka.avservice.common.data

import spock.lang.Specification

/**
 * Enum spec.
 */
class AvMessageTypeSpec extends Specification {

    def "check all values"() {
        expect:
            AvMessageType.REQUEST
            AvMessageType.RESPONSE
            AvMessageType.RESPONSE_ERROR
    }
}
