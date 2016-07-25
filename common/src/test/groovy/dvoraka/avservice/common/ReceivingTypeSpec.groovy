package dvoraka.avservice.common

import spock.lang.Specification

/**
 * ReceivingType spec.
 */
class ReceivingTypeSpec extends Specification {

    def "check all values"() {
        expect:
            ReceivingType.LISTENER
            ReceivingType.POLLING
    }
}
