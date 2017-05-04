package dvoraka.avservice.common.data

import spock.lang.Specification
import spock.lang.Subject

/**
 * Message spec.
 */
class DefaultReplicationMessageSpec extends Specification {

    @Subject
    ReplicationMessage message


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
            MessageRouting testRouting = MessageRouting.BROADCAST
            ReplicationStatus testStatus = ReplicationStatus.READY
            Command testCommand = Command.DISCOVER

        when:
            message = new DefaultReplicationMessage.Builder(null)
                    .correlationId(testCorrId)
                    .type(testType)

                    .data(testData)
                    .filename(testFilename)
                    .owner(testOwner)

                    .fromId(testFromId)
                    .toId(testToId)
                    .sequence(testSequence)
                    .routing(testRouting)
                    .replicationStatus(testStatus)
                    .command(testCommand)
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
            message.getRouting() == testRouting
            message.getReplicationStatus() == testStatus
            message.getCommand() == testCommand
    }

    def "build with ID"() {
        given:
            String testId = 'testID'

        when:
            message = new DefaultReplicationMessage.Builder(testId)
                    .build()

        then:
            message.getId() == testId
    }

    def "to string"() {
        when:
            message = new DefaultReplicationMessage.Builder(null)
                    .build()

        then:
            message.toString().endsWith('}')
    }
}
