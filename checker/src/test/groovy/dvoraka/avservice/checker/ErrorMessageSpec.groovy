package dvoraka.avservice.checker

import dvoraka.avservice.checker.exception.BadExchangeException
import dvoraka.avservice.checker.exception.UnknownProtocolException
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

    def "check call with BadExchangeException"() {
        given:
        String rawMessage = "bad app-id: some reason"
        ErrorMessage message = new ErrorMessage(rawMessage)

        when:
        message.check()

        then:
        thrown(BadExchangeException)
    }

    def "check call with UnknownProtocolException"() {
        given:
        String rawMessage = "some problem: some reason"
        ErrorMessage message = new ErrorMessage(rawMessage)

        when:
        message.check()

        then:
        thrown(UnknownProtocolException)
    }

    def "to string"() {
        given:
        String rawMessage = "some problem: some reason"
        ErrorMessage message = new ErrorMessage(rawMessage)

        when:
        String str = message.toString()

        then:
        str.contains("Error message:")
    }
}
