package dvoraka.avservice.server.replication

import dvoraka.avservice.client.service.ReplicationServiceClient
import dvoraka.avservice.client.service.response.ReplicationMessageList
import dvoraka.avservice.client.service.response.ReplicationResponseClient
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.Command
import dvoraka.avservice.common.data.DefaultAvMessage
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.common.data.MessageRouting
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.common.data.ReplicationMessage
import dvoraka.avservice.common.data.ReplicationStatus
import dvoraka.avservice.common.replication.ReplicationHelper
import dvoraka.avservice.common.runner.ServiceRunner
import dvoraka.avservice.server.runner.amqp.AmqpReplicationServiceRunner
import dvoraka.avservice.storage.configuration.StorageConfig
import dvoraka.avservice.storage.service.FileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * Replication service spec.
 * <p>
 * It uses low-level clients for simulating a service node. A real service node runs on
 * the replication network and is started automatically before the testing. A testing file
 * service uses the same storage as the service node so it is possible to check files after
 * processing directly through the file service.
 */
@Stepwise
@ContextConfiguration(classes = [StorageConfig.class])
@ActiveProfiles(['storage', 'replication-test', 'client', 'amqp', 'db-mem'])
@PropertySource('classpath:avservice.properties')
@DirtiesContext
class ReplicationServiceISpec extends Specification implements ReplicationHelper {

    @Autowired
    ReplicationServiceClient client
    @Autowired
    ReplicationResponseClient responseClient
    @Autowired
    FileService fileService

    @Value('${avservice.storage.replication.testNodeId}')
    String nodeId

    @Shared
    long responseTime = 2_000
    @Shared
    String otherNodeId = 'node1'
    @Shared
    String file = 'replTestFile'
    @Shared
    String owner = 'replTestOwner'

    @Shared
    ServiceRunner runner


    def setup() {
    }

    def setupSpec() {
        AmqpReplicationServiceRunner.setTestRun(false)
        runner = new AmqpReplicationServiceRunner()
        runner.runAsync()
        sleep(6_000) // wait for server start
    }

    def cleanupSpec() {
        runner.stop()
    }

    def "discovery testing"() {
        given:
            ReplicationMessage request = createDiscoverRequest(nodeId)

        when: "send discovery request"
            client.sendMessage(request)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWait(request.getId(), responseTime)

        then: "we should get one response"
            messages.isPresent()
            messages.get().stream().count() == 1

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
                    responseClient.getResponseWait(request.getId(), responseTime)

        then: "we should get one response"
            messages.isPresent()
            messages.get().stream().count() == 1

        and: "check response fields"
            ReplicationMessage response = messages.get().stream().findAny().get()
            response.getType() == MessageType.REPLICATION_SERVICE
            response.getCommand() == Command.SEQUENCE
            response.getRouting() == MessageRouting.UNICAST
            response.getFromId()
            response.getToId() == nodeId
            response.getSequence() == 1
    }

    def "lock request"() {
        given:
            ReplicationMessage request = createLockRequest(file, owner, nodeId, 1)

        when:
            client.sendMessage(request)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWait(request.getId(), responseTime)

        then: "we should get one response"
            messages.isPresent()
            messages.get().stream().count() == 1

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
                    responseClient.getResponseWait(sequenceRequest.getId(), responseTime)

        then: "we should get one sequence response"
            messages.isPresent()
            messages.get().stream().count() == 1

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
                    responseClient.getResponseWait(request.getId(), responseTime)

        then: "we should get one response"
            lockMessages.isPresent()
            lockMessages.get().stream().count() == 1

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
                    responseClient.getResponseWait(request.getId(), responseTime)

        then: "we should get one response"
            lockMessages.isPresent()
            lockMessages.get().stream().count() == 1

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
                    responseClient.getResponseWait(sequenceRequest.getId(), responseTime)

        then: "we should get one sequence response"
            messages.isPresent()
            messages.get().stream().count() == 1

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
                    responseClient.getResponseWait(request.getId(), responseTime)

        then: "we should get one response"
            lockMessages.isPresent()
            lockMessages.get().stream().count() == 1

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
                    responseClient.getResponseWait(unlockRequest.getId(), responseTime)

        then: "we should get one response"
            unlockMessages.isPresent()
            unlockMessages.get().stream().count() == 1

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

        expect: "file not exists"
            !fileService.exists(saveMessage)

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
            saveStatus.getFromId()
            saveStatus.getToId() == nodeId

        and: "file should be stored"
            fileService.exists(saveMessage)

        cleanup:
            // we use a save message which will be probably prohibited in the future
            fileService.deleteFile(saveMessage)
    }

    def "exists"() {
        given:
            FileMessage saveMessage = Utils.genSaveMessage()
            ReplicationMessage saveRequest = createSaveMessage(saveMessage, nodeId, otherNodeId)
            ReplicationMessage existsRequest = createExistsRequest(
                    saveMessage.getFilename(), saveMessage.getOwner(), nodeId)

        expect: "file not exists"
            !fileService.exists(saveMessage)

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

        and: "file should be stored"
            fileService.exists(saveMessage)

        when:
            client.sendMessage(existsRequest)
            messages = responseClient.getResponseWait(existsRequest.getId(), responseTime)

        then:
            messages.isPresent()
            messages.get().stream().count() == 1

        when:
            ReplicationMessage existsStatus = messages.get().stream().findFirst().get()

        then:
            existsStatus.getType() == MessageType.REPLICATION_SERVICE
            existsStatus.getCommand() == Command.EXISTS
            existsStatus.getRouting() == MessageRouting.UNICAST
            existsStatus.getReplicationStatus() == ReplicationStatus.OK
            existsStatus.getFromId() == otherNodeId
            existsStatus.getToId() == nodeId

        cleanup:
            // we use a save message which will be probably prohibited in the future
            fileService.deleteFile(saveMessage)
    }

    def "load file"() {
        given:
            FileMessage saveMessage = Utils.genSaveMessage()
            FileMessage loadMessage = new DefaultAvMessage.Builder(Utils.genUuidString())
                    .type(MessageType.FILE_LOAD)
                    .filename(saveMessage.getFilename())
                    .owner(saveMessage.getOwner())
                    .build();

            ReplicationMessage saveRequest = createSaveMessage(saveMessage, nodeId, otherNodeId)
            ReplicationMessage loadRequest = createLoadMessage(loadMessage, nodeId, otherNodeId)

        expect: "file not exists"
            !fileService.exists(saveMessage)

        when: "save generated file"
            client.sendMessage(saveRequest)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWait(saveRequest.getId(), responseTime)

        then: "response should be OK and file exist"
            messages.isPresent()
            fileService.exists(saveMessage)

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
            loadReponse.getFromId()
            loadReponse.getToId() == nodeId

        and:
            loadReponse.getFilename() == saveMessage.getFilename()
            loadReponse.getOwner() == saveMessage.getOwner()
            Arrays.equals(loadReponse.getData(), saveMessage.getData())

        cleanup:
            // we use a save message which will be probably prohibited in the future
            fileService.deleteFile(saveMessage)
    }

    @Ignore('WIP')
    def "delete file"() {
        given:
            FileMessage fileMessage = Utils.genFileMessage()
            ReplicationMessage deleteRequest = createDeleteMessage(fileMessage, nodeId, otherNodeId)

        when:
            client.sendMessage(deleteRequest)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWait(deleteRequest.getId(), responseTime)

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
            deleteStatus.getFromId()
            deleteStatus.getToId() == nodeId
    }

    void unlockTestFile() {
        // sequence is not checked now
        ReplicationMessage unlockRequest = createUnlockRequest(file, owner, nodeId, 0)
        client.sendMessage(unlockRequest)
    }
}
