package dvoraka.avservice.db.model

import spock.lang.Specification
import spock.lang.Subject

/**
 * Message info document spec.
 */
class MessageInfoDocumentSpec extends Specification {

    @Subject
    MessageInfoDocument document


    def setup() {
        document = new MessageInfoDocument()
    }

    def "set and get"() {
        given:
            String testVal = "TEST"
            Date testDate = new Date()

        when:
            document.with {
                setId(testVal)
                setUuid(testVal)
                setSource(testVal)
                setServiceId(testVal)
                setCreated(testDate)
            }

        then:
            with(document) {
                getId() == testVal
                getUuid() == testVal
                getSource() == testVal
                getServiceId() == testVal
                getCreated() == testDate
            }
    }
}
