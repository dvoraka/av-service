package dvoraka.avservice.common.data

import spock.lang.Specification

/**
 * AV message source enum spec.
 */
class AvMessageSourceSpec extends Specification {

    def "check all values"() {
        expect:
            AvMessageSource.CUSTOM
            AvMessageSource.AMQP_COMPONENT
            AvMessageSource.AMQP_COMPONENT_IN
            AvMessageSource.AMQP_COMPONENT_OUT
            AvMessageSource.JMS_COMPONENT
            AvMessageSource.JMS_COMPONENT_IN
            AvMessageSource.JMS_COMPONENT_OUT
            AvMessageSource.SERVER
            AvMessageSource.PROCESSOR
            AvMessageSource.TEST
    }
}
