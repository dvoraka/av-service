package dvoraka.avservice.common.data

import spock.lang.Specification

/**
 * Message status test.
 */
class MessageStatusSpec extends Specification {

    def "test all values"() {
        expect:
        MessageStatus.WAITING
        MessageStatus.PROCESSING
        MessageStatus.PROCESSED
        MessageStatus.UNKNOWN
    }
}
