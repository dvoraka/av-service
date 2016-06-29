package dvoraka.avservice.checker

import spock.lang.Specification

/**
 * Error message test.
 */
class ErrorMessageSpec extends Specification {

    def "constructor with a null value"() {
        when:
        new ErrorMessage(null)

        then:
        thrown(IllegalArgumentException)
    }

    def "constructor (rawMessage)"() {
        given:
        String msgType = "message type:"
        String msgText = "info"
        String rawMessage = msgType + msgText

        when:
        ErrorMessage message = new ErrorMessage(rawMessage)

        then:
        message.getErrorType().equals(msgType.split(":")[0])
        message.getErrorText().equals(rawMessage)
    }
}
