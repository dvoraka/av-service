package dvoraka.avservice.server.replication

import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.client.service.ReplicationServiceClient
import dvoraka.avservice.client.service.response.ReplicationMessageList
import dvoraka.avservice.client.service.response.ReplicationResponseClient
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.Command
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.common.data.MessageRouting
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.common.data.ReplicationMessage
import dvoraka.avservice.common.data.ReplicationStatus
import dvoraka.avservice.common.runner.ServiceRunner
import dvoraka.avservice.server.runner.amqp.AmqpReplicationServiceRunner
import dvoraka.avservice.storage.replication.ReplicationHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

/**
 * Replication service spec.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['replication-test', 'client', 'amqp', 'amqp-client', 'no-db'])
@PropertySource('classpath:avservice.properties')
@Ignore('WIP')
@DirtiesContext
class ReplicationServiceISpec extends Specification implements ReplicationHelper {

    @Autowired
    ReplicationServiceClient client
    @Autowired
    ReplicationResponseClient responseClient

    @Value('${avservice.storage.replication.testNodeId}')
    String nodeId

    long responseTime = 2_000
    String otherNodeId = 'node1'

    @Shared
    ServiceRunner runner


    def setup() {
    }

    def setupSpec() {
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
            response.getSequence()
    }

    def "lock request"() {
        given:
            String file = 'replTestFile'
            String owner = 'replTestOwner'
            ReplicationMessage request = createLockRequest(file, owner, nodeId, 99)

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
            response.getFromId()
            response.getToId() == nodeId
            response.getSequence() == request.getSequence()
    }

    def "lock file"() {
        given:
            String file = 'replTestFile'
            String owner = 'replTestOwner'
            ReplicationMessage sequenceRequest = createSequenceRequest(nodeId)
            long sequence = 0

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
    }

    def "lock file with bad sequence"() {
        given:
            String file = 'replTestFile'
            String owner = 'replTestOwner'
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
            response.getSequence() == request.getSequence()
    }

    def "lock and unlock file"() {
        given:
            String file = 'replTestFile'
            String owner = 'replTestOwner'
            ReplicationMessage sequenceRequest = createSequenceRequest(nodeId)
            long sequence = 0

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
            ReplicationMessage unlockRequest = createUnLockRequest(nodeId, sequence, file, owner)
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
            FileMessage fileMessage = Utils.genFileMessage()
            ReplicationMessage saveRequest = createSaveMessage(fileMessage, nodeId, otherNodeId)

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
    }
}
