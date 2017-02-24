package dvoraka.avservice.db.model

import spock.lang.Specification
import spock.lang.Subject

import java.time.Instant

/**
 * Message info spec.
 */
class MessageInfoSpec extends Specification {

    @Subject
    MessageInfo messageInfo


    def setup() {
        messageInfo = new MessageInfo()
    }

    def "set and get"() {
        given:
            Long dbId = 9
            String testVal = "TEST"
            Instant testDate = Instant.now()

        when:
            messageInfo.with {
                setId(dbId)
                setUuid(testVal)
                setSource(testVal)
                setServiceId(testVal)
                setCreated(testDate)
            }

        then:
            with(messageInfo) {
                getId() == dbId
                getUuid() == testVal
                getSource() == testVal
                getServiceId() == testVal
                getCreated() == testDate
            }
    }
}
