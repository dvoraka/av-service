package dvoraka.avservice.common.data

import spock.lang.Specification

/**
 * Message spec.
 */
class DefaultReplicationMessageSpec extends Specification {

    def "build message"() {
        given:
            String testCorrId = 'corrId'
            MessageType testType = MessageType.REPLICATION_SERVICE
            byte[] testData = new byte[10]

        when:
            ReplicationMessage message = new DefaultReplicationMessage.Builder(null)
                    .correlationId(testCorrId)
                    .type(testType)
                    .data(testData)
                    .build()

        then:
            message.getId()
            message.getCorrelationId() == testCorrId
            message.getType() == testType
            Arrays.equals(message.getData(), new byte[10])
    }
}
