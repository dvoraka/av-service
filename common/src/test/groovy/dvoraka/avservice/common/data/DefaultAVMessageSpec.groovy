package dvoraka.avservice.common.data

import spock.lang.Specification

/**
 * DefaultAVMessage test.
 */
class DefaultAVMessageSpec extends Specification {


    def "create response test"() {
        setup:
        DefaultAVMessage message = new DefaultAVMessage.Builder('TEST-ID').build()
        String expCorrId = message.getId()

        AVMessage response = message.createResponse(false)

        expect:
        response.getCorrelationId().equals(expCorrId)
        response.getType().equals(AVMessageType.RESPONSE)
    }

    def "simple toString test"() {
        setup:
        DefaultAVMessage message = new DefaultAVMessage.Builder('TEST-ID').build()

        expect:
        message.toString().startsWith("DefaultAVMessage {")
        message.toString().endsWith("}")
    }
}
