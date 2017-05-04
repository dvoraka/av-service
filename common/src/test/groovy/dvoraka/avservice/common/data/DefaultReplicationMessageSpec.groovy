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
            String testFilename = 'testFilename'
            String testOwner = 'testOwner'

            String testFromId = 'testFromId'
            String testToId = 'testToId'
            long testSequence = 999L

        when:
            ReplicationMessage message = new DefaultReplicationMessage.Builder(null)
                    .correlationId(testCorrId)
                    .type(testType)

                    .data(testData)
                    .filename(testFilename)
                    .owner(testOwner)

                    .fromId(testFromId)
                    .toId(testToId)
                    .sequence(testSequence)
                    .build()

        then:
            message.getId()
            message.getCorrelationId() == testCorrId
            message.getType() == testType

            Arrays.equals(message.getData(), new byte[10])
            message.getFilename() == testFilename
            message.getOwner() == testOwner

            message.getFromId() == testFromId
            message.getToId() == testToId
            message.getSequence() == testSequence
    }
}
