package dvoraka.avservice.common.data

import dvoraka.avservice.common.helper.FileServiceHelper
import dvoraka.avservice.common.replication.ReplicationHelper
import dvoraka.avservice.common.replication.ReplicationServiceHelper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

/**
 * Message spec.
 */
class DefaultReplicationMessageSpec extends Specification
        implements ReplicationServiceHelper, ReplicationHelper, FileServiceHelper {

    @Subject
    ReplicationMessage message

    @Shared
    String testFilename = 'testFilename'
    @Shared
    String testOwner = 'testOwner'
    @Shared
    String testFromId = 'testFromId'
    @Shared
    String testToId = 'testToId'


    def "build message"() {
        given:
            String testCorrId = 'corrId'
            MessageType testType = MessageType.REPLICATION_SERVICE

            byte[] testData = new byte[10]

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

    def "file message"() {
        given:
            ReplicationMessage exists = createExistsRequest(testFilename, testOwner, testFromId)
            ReplicationMessage loadMessage = createLoadMessage(
                    fileLoadMessage(testFilename, testOwner), testFromId, testToId)

        expect:
            exists.fileMessage() == null

        when:
            FileMessage transformed = loadMessage.fileMessage()

        then:
            transformed.getFilename() == loadMessage.getFilename()
            transformed.getOwner() == loadMessage.getOwner()
    }
}
