package dvoraka.avservice

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.MessageType
import spock.lang.Specification
import spock.lang.Subject

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
}
