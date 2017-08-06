package dvoraka.avservice.storage.replication

import dvoraka.avservice.client.service.ReplicationServiceClient
import dvoraka.avservice.client.service.response.ReplicationMessageList
import dvoraka.avservice.client.service.response.ReplicationResponseClient
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.Command
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.common.data.replication.MessageRouting
import dvoraka.avservice.common.data.replication.ReplicationMessage
import dvoraka.avservice.common.data.replication.ReplicationStatus
import dvoraka.avservice.common.helper.FileServiceHelper
import dvoraka.avservice.common.replication.ReplicationHelper
import dvoraka.avservice.storage.configuration.StorageConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * Replication service spec 2.
 * <p>
 * It uses low-level clients for simulating a service node.
 */
@Stepwise
@ContextConfiguration(classes = [StorageConfig.class])
@ActiveProfiles(['storage', 'replication-test', 'client', 'amqp', 'no-db'])
@PropertySource('classpath:avservice.properties')
@DirtiesContext
class ReplicationService2ISpec extends Specification
        implements ReplicationHelper, FileServiceHelper {

    @Autowired
    ReplicationServiceClient client
    @Autowired
    ReplicationResponseClient responseClient

    @Value('${avservice.storage.replication.testNodeId}')
    String nodeId

    long actualSequence

    @Shared
    long responseTime = 2_000
    @Shared
    int nodeCount = 2
    @Shared
    String otherNodeId = 'node1000'
    @Shared
    String file = 'replTestFile'
    @Shared
    String owner = 'replTestOwner'


    def cleanupSpec() {
        sleep(1_000)
    }

    def setup() {
        ReplicationMessage request = createSequenceRequest(nodeId)
        client.sendMessage(request)
        Optional<ReplicationMessageList> messages = responseClient
                .getResponseWait(request.getId(), responseTime)

        actualSequence = messages
                .orElse(new ReplicationMessageList())
                .stream()
                .findFirst()
                .map({ m -> m.getSequence() })
                .orElse(-2L)
    }

    def "discovery testing"() {
        given:
            ReplicationMessage request = createDiscoverRequest(nodeId)
            // wait for client initializations
            sleep(2_000)

        when: "send discovery request"
            client.sendMessage(request)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWaitSize(request.getId(), responseTime, nodeCount)

        then:
            messages.isPresent()
            messages.get().stream().count() == nodeCount

        and: "check response fields"
            ReplicationMessage response = messages.get().stream().findAny().get()
            response.getType() == MessageType.REPLICATION_SERVICE
            response.getCommand() == Command.DISCOVER
            response.getReplicationStatus() == ReplicationStatus.READY
            response.getRouting() == MessageRouting.UNICAST
            response.getFromId()
            response.getToId() == nodeId
    }

    def "synchronization testing"() {
        given:
            ReplicationMessage request = createSequenceRequest(nodeId)

        when: "send sequence request"
            client.sendMessage(request)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWaitSize(request.getId(), responseTime, nodeCount)

        then:
            messages.isPresent()
            messages.get().stream().count() == nodeCount

        and: "check response fields"
            ReplicationMessage response = messages.get().stream().findAny().get()
            response.getType() == MessageType.REPLICATION_SERVICE
            response.getCommand() == Command.SEQUENCE
            response.getRouting() == MessageRouting.UNICAST
            response.getFromId()
            response.getToId() == nodeId
            response.getSequence() == actualSequence
    }

    def "lock request"() {
        given:
            ReplicationMessage request = createLockRequest(file, owner, nodeId, actualSequence)

        when:
            client.sendMessage(request)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWaitSize(request.getId(), responseTime, nodeCount)

        then:
            messages.isPresent()
            messages.get().stream().count() == nodeCount

        and:
            ReplicationMessage response = messages.get().stream().findAny().get()
            response.getType() == MessageType.REPLICATION_SERVICE
            response.getCommand() == Command.LOCK
            response.getRouting() == MessageRouting.UNICAST
            response.getReplicationStatus() == ReplicationStatus.READY
            response.getFromId()
            response.getToId() == nodeId
            response.getSequence() == request.getSequence()

        cleanup:
            unlockTestFile()
    }

    def "lock file"() {
        given:
            ReplicationMessage sequenceRequest = createSequenceRequest(nodeId)
            long sequence = 0 // Spock needs initialization

        when: "get actual sequence"
            client.sendMessage(sequenceRequest)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWaitSize(
                            sequenceRequest.getId(), responseTime, nodeCount)

        then:
            messages.isPresent()
            messages.get().stream().count() == nodeCount

        when:
            ReplicationMessage sequenceResponse = messages.get().stream().findAny().get()

        then:
            sequenceResponse.getCommand() == Command.SEQUENCE
            sequenceResponse.getRouting() == MessageRouting.UNICAST
            sequenceResponse.getFromId()
            sequenceResponse.getToId() == nodeId
            sequenceResponse.getSequence()

        when: "try to lock file"
            sequence = sequenceResponse.getSequence()
            ReplicationMessage request = createLockRequest(file, owner, nodeId, sequence)
            client.sendMessage(request)
            Optional<ReplicationMessageList> lockMessages =
                    responseClient.getResponseWaitSize(request.getId(), responseTime, nodeCount)

        then:
            lockMessages.isPresent()
            lockMessages.get().stream().count() == nodeCount

        when:
            ReplicationMessage response = lockMessages.get().stream().findAny().get()

        then:
            response.getType() == MessageType.REPLICATION_SERVICE
            response.getCommand() == Command.LOCK
            response.getRouting() == MessageRouting.UNICAST
            response.getReplicationStatus() == ReplicationStatus.READY
            response.getFromId()
            response.getToId() == nodeId
            response.getSequence() == request.getSequence()

        cleanup:
            unlockTestFile()
    }

    def "lock file with bad sequence"() {
        given:
            ReplicationMessage sequenceRequest = createSequenceRequest(nodeId)
            long sequence = 99

        when: "try to lock file"
            ReplicationMessage request = createLockRequest(file, owner, nodeId, sequence)
            client.sendMessage(request)
            Optional<ReplicationMessageList> lockMessages =
                    responseClient.getResponseWaitSize(request.getId(), responseTime, nodeCount)

        then:
            lockMessages.isPresent()
            lockMessages.get().stream().count() == nodeCount

        when:
            ReplicationMessage response = lockMessages.get().stream().findAny().get()

        then:
            response.getType() == MessageType.REPLICATION_SERVICE
            response.getCommand() == Command.LOCK
            response.getRouting() == MessageRouting.UNICAST
            response.getReplicationStatus() == ReplicationStatus.FAILED
            response.getFromId()
            response.getToId() == nodeId
            response.getSequence()
    }

    def "lock and unlock file"() {
        given:
            ReplicationMessage sequenceRequest = createSequenceRequest(nodeId)
            long sequence

        when: "get actual sequence"
            client.sendMessage(sequenceRequest)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWaitSize(
                            sequenceRequest.getId(), responseTime, nodeCount)

        then:
            messages.isPresent()
            messages.get().stream().count() == nodeCount

        when:
            ReplicationMessage sequenceResponse = messages.get().stream().findAny().get()

        then:
            sequenceResponse.getCommand() == Command.SEQUENCE
            sequenceResponse.getRouting() == MessageRouting.UNICAST
            sequenceResponse.getFromId()
            sequenceResponse.getToId() == nodeId
            sequenceResponse.getSequence()

        when: "try to lock file"
            sequence = sequenceResponse.getSequence()
            ReplicationMessage request = createLockRequest(file, owner, nodeId, sequence)
            client.sendMessage(request)
            Optional<ReplicationMessageList> lockMessages =
                    responseClient.getResponseWaitSize(request.getId(), responseTime, nodeCount)

        then:
            lockMessages.isPresent()
            lockMessages.get().stream().count() == nodeCount

        when:
            ReplicationMessage response = lockMessages.get().stream().findAny().get()

        then:
            response.getType() == MessageType.REPLICATION_SERVICE
            response.getCommand() == Command.LOCK
            response.getRouting() == MessageRouting.UNICAST
            response.getReplicationStatus() == ReplicationStatus.READY
            response.getFromId()
            response.getToId() == nodeId
            response.getSequence() == request.getSequence()

        when: "try to unlock the locked file"
            ReplicationMessage unlockRequest = createUnlockRequest(file, owner, nodeId, sequence)
            client.sendMessage(unlockRequest)
            Optional<ReplicationMessageList> unlockMessages =
                    responseClient.getResponseWaitSize(
                            unlockRequest.getId(), responseTime, nodeCount)

        then:
            unlockMessages.isPresent()
            unlockMessages.get().stream().count() == nodeCount

        when:
            ReplicationMessage unlockResponse = unlockMessages.get().stream().findAny().get()

        then:
            unlockResponse.getType() == MessageType.REPLICATION_SERVICE
            unlockResponse.getCommand() == Command.UNLOCK
            unlockResponse.getRouting() == MessageRouting.UNICAST
            unlockResponse.getReplicationStatus() == ReplicationStatus.OK
            unlockResponse.getFromId()
            unlockResponse.getToId() == nodeId
            unlockResponse.getFilename() == file
            unlockResponse.getOwner() == owner
    }

    def "save file"() {
        given:
            FileMessage saveMessage = Utils.genSaveMessage()
            ReplicationMessage saveRequest = createSaveMessage(saveMessage, nodeId, otherNodeId)

        when:
            client.sendMessage(saveRequest)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWait(saveRequest.getId(), responseTime)

        then: "we should get one file response"
            messages.isPresent()
            messages.get().stream().count() == 1

        when:
            ReplicationMessage saveStatus = messages.get().stream().findFirst().get()

        then:
            saveStatus.getType() == MessageType.REPLICATION_COMMAND
            saveStatus.getCommand() == Command.SAVE
            saveStatus.getRouting() == MessageRouting.UNICAST
            saveStatus.getReplicationStatus() == ReplicationStatus.OK
            saveStatus.getFromId() == otherNodeId
            saveStatus.getToId() == nodeId
    }

    def "exists"() {
        given:
            FileMessage saveMessage = Utils.genSaveMessage()
            ReplicationMessage saveRequest = createSaveMessage(saveMessage, nodeId, otherNodeId)
            ReplicationMessage existsRequest = createExistsRequest(
                    saveMessage.getFilename(), saveMessage.getOwner(), nodeId)

        when:
            client.sendMessage(saveRequest)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWait(saveRequest.getId(), responseTime)

        then: "we should get one file response"
            messages.isPresent()
            messages.get().stream().count() == 1

        when:
            ReplicationMessage saveStatus = messages.get().stream().findFirst().get()

        then:
            saveStatus.getType() == MessageType.REPLICATION_COMMAND
            saveStatus.getCommand() == Command.SAVE
            saveStatus.getRouting() == MessageRouting.UNICAST
            saveStatus.getReplicationStatus() == ReplicationStatus.OK
            saveStatus.getFromId() == otherNodeId
            saveStatus.getToId() == nodeId

        when:
            client.sendMessage(existsRequest)
            messages = responseClient.getResponseWaitSize(
                    existsRequest.getId(), responseTime, nodeCount)

        then:
            messages.isPresent()
            messages.get().stream()
                    .filter({ msg -> msg.getFromId() == otherNodeId })
                    .count() == 1

        when:
            ReplicationMessage existsStatus = messages.get().stream()
                    .filter({ msg -> msg.getFromId() == otherNodeId })
                    .findFirst().get()

        then:
            existsStatus.getType() == MessageType.REPLICATION_SERVICE
            existsStatus.getCommand() == Command.EXISTS
            existsStatus.getRouting() == MessageRouting.UNICAST
            existsStatus.getReplicationStatus() == ReplicationStatus.OK
            existsStatus.getFromId() == otherNodeId
            existsStatus.getToId() == nodeId
    }

    def "load file"() {
        given:
            FileMessage saveMessage = Utils.genSaveMessage()
            FileMessage loadMessage = fileLoadMessage(saveMessage)

            ReplicationMessage saveRequest = createSaveMessage(saveMessage, nodeId, otherNodeId)
            ReplicationMessage loadRequest = createLoadMessage(loadMessage, nodeId, otherNodeId)

        when: "save generated file"
            client.sendMessage(saveRequest)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWait(saveRequest.getId(), responseTime)

        then: "response should be OK and file exist"
            messages.isPresent()

        when:
            client.sendMessage(loadRequest)
            messages = responseClient.getResponseWait(loadRequest.getId(), responseTime)

        then: "we should get one load response"
            messages.isPresent()
            messages.get().stream().count() == 1

        when:
            ReplicationMessage loadReponse = messages.get().stream().findFirst().get()

        then:
            loadReponse.getType() == MessageType.REPLICATION_COMMAND
            loadReponse.getCommand() == Command.LOAD
            loadReponse.getRouting() == MessageRouting.UNICAST
            loadReponse.getReplicationStatus() == ReplicationStatus.OK
            loadReponse.getFromId() == otherNodeId
            loadReponse.getToId() == nodeId

        and:
            loadReponse.getFilename() == saveMessage.getFilename()
            loadReponse.getOwner() == saveMessage.getOwner()
            Arrays.equals(loadReponse.getData(), saveMessage.getData())
    }

    def "delete file"() {
        given:
            FileMessage saveMessage = Utils.genSaveMessage()
            ReplicationMessage saveRequest = createSaveMessage(saveMessage, nodeId, otherNodeId)

            FileMessage deleteMessage = fileDeleteMessage(saveMessage)
            ReplicationMessage deleteRequest = createDeleteMessage(
                    deleteMessage, nodeId, otherNodeId)

        when:
            client.sendMessage(saveRequest)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWait(saveRequest.getId(), responseTime)

        then: "we should get one file response"
            messages.isPresent()
            messages.get().stream().count() == 1

        when:
            ReplicationMessage saveStatus = messages.get().stream().findFirst().get()

        then:
            saveStatus.getType() == MessageType.REPLICATION_COMMAND
            saveStatus.getCommand() == Command.SAVE
            saveStatus.getRouting() == MessageRouting.UNICAST
            saveStatus.getReplicationStatus() == ReplicationStatus.OK
            saveStatus.getFromId() == otherNodeId
            saveStatus.getToId() == nodeId

        when:
            client.sendMessage(deleteRequest)
            messages = responseClient.getResponseWait(deleteRequest.getId(), responseTime)

        then: "we should get one file response"
            messages.isPresent()
            messages.get().stream().count() == 1

        when:
            ReplicationMessage deleteStatus = messages.get().stream().findFirst().get()

        then:
            deleteStatus.getType() == MessageType.REPLICATION_COMMAND
            deleteStatus.getCommand() == Command.DELETE
            deleteStatus.getRouting() == MessageRouting.UNICAST
            deleteStatus.getReplicationStatus() == ReplicationStatus.OK
            deleteStatus.getFromId() == otherNodeId
            deleteStatus.getToId() == nodeId
    }

    void unlockTestFile() {
        // sequence is not checked now
        ReplicationMessage unlockRequest = createUnlockRequest(file, owner, nodeId, 0)
        client.sendMessage(unlockRequest)
    }
}
