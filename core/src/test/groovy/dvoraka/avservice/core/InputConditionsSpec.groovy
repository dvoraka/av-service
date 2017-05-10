package dvoraka.avservice.core

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.MessageType
import spock.lang.Specification
import spock.lang.Subject

import java.util.function.BiPredicate

/**
 * Input condition spec.
 */
class InputConditionsSpec extends Specification {

    @Subject
    InputConditions conditions


    def setup() {
    }

    def "allowed original types passed"() {
        given:
            conditions = new InputConditions.Builder()
                    .originalType(MessageType.FILE_UPDATE)
                    .originalType(MessageType.FILE_RESPONSE)
                    .originalType(MessageType.FILE_SAVE)
                    .build()

            AvMessage fileMessage = Utils.genFileMessage()

        expect:
            conditions.test(fileMessage, fileMessage)
    }

    def "allowed original types failed"() {
        given:
            conditions = new InputConditions.Builder()
                    .originalType(MessageType.FILE_UPDATE)
                    .originalType(MessageType.FILE_RESPONSE)
                    .build()

            AvMessage normalMessage = Utils.genMessage()

        expect:
            !conditions.test(normalMessage, normalMessage)
    }

    def "allowed last types passed"() {
        given:
            conditions = new InputConditions.Builder()
                    .lastType(MessageType.FILE_RESPONSE)
                    .lastType(MessageType.FILE_NOT_FOUND)
                    .lastType(MessageType.FILE_SAVE)
                    .build()

            AvMessage fileMessage = Utils.genFileMessage()

        expect:
            conditions.test(fileMessage, fileMessage)
    }

    def "allowed last types failed"() {
        given:
            conditions = new InputConditions.Builder()
                    .lastType(MessageType.FILE_RESPONSE)
                    .lastType(MessageType.FILE_NOT_FOUND)
                    .lastType(MessageType.FILE_SAVE)
                    .build()

            AvMessage normalMessage = Utils.genMessage()

        expect:
            !conditions.test(normalMessage, normalMessage)
    }

    def "test condition"() {
        given:

            BiPredicate<AvMessage, AvMessage> cond = new BiPredicate<AvMessage, AvMessage>() {
                @Override
                boolean test(AvMessage orig, AvMessage last) {
                    return last.getVirusInfo() != null
                }
            }

            conditions = new InputConditions.Builder()
                    .lastType(MessageType.RESPONSE)
                    .condition(cond)
                    .build()

            AvMessage responseMessage = Utils.genMessage().createCheckResponse("TEST")

        expect:
            conditions.test(responseMessage, responseMessage)
    }
}
