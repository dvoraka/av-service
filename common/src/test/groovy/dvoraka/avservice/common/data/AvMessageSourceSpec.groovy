package dvoraka.avservice.common.data

import spock.lang.Specification

/**
 * Enum spec
 */
class AvMessageSourceSpec extends Specification {

    def "check all values"() {
        expect:
            AvMessageSource.CUSTOM
            AvMessageSource.AMQP_COMPONENT
            AvMessageSource.SERVER
            AvMessageSource.PROCESSOR
            AvMessageSource.TEST
    }
}
