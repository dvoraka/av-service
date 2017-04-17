package dvoraka.avservice.common.service

import dvoraka.avservice.common.data.MessageStatus
import spock.lang.Specification
import spock.lang.Subject

/**
 * BasicMessageStatusStorage spec.
 */
class BasicMessageStatusStorageSpec extends Specification {

    @Subject
    BasicMessageStatusStorage storage


    def setup() {
        storage = new BasicMessageStatusStorage(2_000L)
    }

    def cleanup() {
        storage.stop()
    }

    def "test storage"() {
        given:
            String testId = 'test'

        when:
            storage.started(testId)

        then:
            storage.getStatus(testId) == MessageStatus.PROCESSING

        when:
            storage.processed(testId)

        then:
            storage.getStatus(testId) == MessageStatus.PROCESSED

        when:
            storage.stop()

        then:
            notThrown(Exception)
    }

    def "get status for unknown ID"() {
        expect:
            storage.getStatus('unknown') == MessageStatus.UNKNOWN
    }
}
