package dvoraka.avservice.storage.replication

import dvoraka.avservice.client.service.ReplicationServiceClient
import dvoraka.avservice.client.service.response.ReplicationMessageList
import dvoraka.avservice.client.service.response.ReplicationResponseClient
import dvoraka.avservice.common.data.Command
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.common.data.replication.MessageRouting
import dvoraka.avservice.common.data.replication.ReplicationMessage
import dvoraka.avservice.common.data.replication.ReplicationStatus
import dvoraka.avservice.common.helper.FileServiceHelper
import dvoraka.avservice.common.helper.WaitingHelper
import dvoraka.avservice.common.helper.replication.ReplicationHelper
import dvoraka.avservice.storage.configuration.StorageConfig
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
 * Remote lock spec. Other specs are in Replication service spec.
 * <p>
 * It uses low-level clients for simulating a service node.
 */
@ContextConfiguration(classes = [StorageConfig.class])
@ActiveProfiles(['storage', 'replication-test', 'client', 'amqp', 'no-db'])
@PropertySource('classpath:avservice.properties')
@DirtiesContext
//TODO: #318
@Ignore
class RemoteLockISpec extends Specification
        implements ReplicationHelper, FileServiceHelper, WaitingHelper {

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
    String file = 'replicationTestFile'
    @Shared
    String owner = 'replicationTestOwner'


    def setup() {
        waitUntil({ responseClient.isRunning() })

        ReplicationMessage request = createSequenceRequest(nodeId)
        client.sendMessage(request)
        Optional<ReplicationMessageList> messages = responseClient
                .getResponseWait(request.getId(), responseTime)

        actualSequence = messages
                .orElse(new ReplicationMessageList())
                .stream()
                .findFirst()
                .map({ m -> m.getSequence() })
                .orElse(0L)
    }

    def "lock request twice for same file"() {
        given:
            ReplicationMessage request = createLockRequest(file, owner, nodeId, actualSequence)
            ReplicationMessage request2 = createLockRequest(file, owner, nodeId, actualSequence + 1)

        when:
            client.sendMessage(request)
            Optional<ReplicationMessageList> messages = responseClient.getResponseWaitSize(
                    request.getId(), responseTime, nodeCount)

        then:
            messages.isPresent()
            messages.get().stream().count() == nodeCount

        and:
            ReplicationMessage response = messages.get().stream()
                    .filter({ msg -> msg.getFromId() == otherNodeId })
                    .findFirst().get()
            checkOkResponse(response)
            response.getSequence() == request.getSequence()

        when:
            client.sendMessage(request2)
            messages = responseClient.getResponseWaitSize(
                    request2.getId(), responseTime, nodeCount)

        then:
            messages.isPresent()
            messages.get().stream().count() == nodeCount

        and:
            ReplicationMessage response2 = messages.get().stream()
                    .filter({ msg -> msg.getFromId() == otherNodeId })
                    .findFirst().get()
            checkFailedResponse(response2)
            response2.getSequence() == request2.getSequence()

        cleanup:
            unlockTestFile()
    }

    def "two lock requests for same file at once"() {
        given:
            ReplicationMessage request = createLockRequest(file, owner, nodeId, actualSequence)
            ReplicationMessage request2 = createLockRequest(file, owner, nodeId, actualSequence)

        when:
            client.sendMessage(request)
            client.sendMessage(request2)

            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWaitSize(request.getId(), responseTime, nodeCount)
            Optional<ReplicationMessageList> messages2 =
                    responseClient.getResponseWaitSize(request.getId(), responseTime, nodeCount)

        then:
            messages.isPresent()
            messages.get().stream().count() == nodeCount
            messages2.isPresent()
            messages2.get().stream().count() == nodeCount

        cleanup:
            unlockTestFile()
    }

    @Ignore("manual testing")
    def "a lot of requests for same file at once"() {
        given:
            List<Thread> requests = new ArrayList<>()
            int threads = 100
            String ownerName = owner
            threads.times {
                requests.add(new Thread({
                    ReplicationMessage request = createLockRequest(file, ownerName, nodeId, 3);
                    client.sendMessage(request)
                }))
            }

        expect:
            requests.each { it.start() }
            requests.each { it.join() }

        cleanup:
            unlockTestFile()
    }

    def "lock request with right and wrong sequence"() {
        given:
            ReplicationMessage request = createLockRequest(file, owner, nodeId, actualSequence)
            ReplicationMessage request2 = createLockRequest(file, owner, nodeId, actualSequence + 2)

        when:
            client.sendMessage(request)
            Optional<ReplicationMessageList> messages = responseClient.getResponseWaitSize(
                    request.getId(), responseTime, nodeCount)

        then:
            messages.isPresent()
            messages.get().stream().count() == nodeCount

        and:
            ReplicationMessage response = messages.get().stream()
                    .filter({ msg -> msg.getFromId() == otherNodeId })
                    .findFirst()
                    .get()
            checkOkResponse(response)
            response.getSequence() == request.getSequence()

        when:
            client.sendMessage(request2)
            messages = responseClient.getResponseWaitSize(
                    request2.getId(), responseTime, nodeCount)

        then:
            messages.isPresent()
            messages.get().stream().count() == nodeCount

        and:
            ReplicationMessage response2 = messages.get().stream()
                    .filter({ msg -> msg.getFromId() == otherNodeId })
                    .findFirst()
                    .get()
            checkFailedResponse(response2)
            response2.getSequence() == request.getSequence() + 1

        cleanup:
            unlockTestFile()
    }

    def "lock two different files"() {
        given:
            ReplicationMessage request = createLockRequest(file, owner, nodeId, actualSequence)
            String otherFile = file + "2"
            ReplicationMessage request2 = createLockRequest(
                    otherFile, owner, nodeId, actualSequence + 1)

        when:
            client.sendMessage(request)
            Optional<ReplicationMessageList> messages = responseClient.getResponseWaitSize(
                    request.getId(), responseTime, nodeCount)

        then:
            messages.isPresent()
            messages.get().stream().count() == nodeCount

        and:
            ReplicationMessage response = messages.get().stream()
                    .filter({ msg -> msg.getFromId() == otherNodeId })
                    .findFirst()
                    .get()
            checkOkResponse(response)
            response.getSequence() == request.getSequence()

        when:
            client.sendMessage(request2)
            messages = responseClient.getResponseWaitSize(
                    request2.getId(), responseTime, nodeCount)

        then:
            messages.isPresent()
            messages.get().stream().count() == nodeCount

        and:
            ReplicationMessage response2 = messages.get().stream()
                    .filter({ msg -> msg.getFromId() == otherNodeId })
                    .findFirst()
                    .get()
            checkOkResponse(response2)
            response2.getSequence() == request2.getSequence()

        cleanup:
            unlockTestFile()
            unlockFile(otherFile)
    }

    void checkOkResponse(ReplicationMessage message) {
        assert message.getType() == MessageType.REPLICATION_SERVICE
        assert message.getCommand() == Command.LOCK
        assert message.getRouting() == MessageRouting.UNICAST
        assert message.getReplicationStatus() == ReplicationStatus.READY
        assert message.getFromId() == otherNodeId
        assert message.getToId() == nodeId
    }

    void checkFailedResponse(ReplicationMessage message) {
        assert message.getType() == MessageType.REPLICATION_SERVICE
        assert message.getCommand() == Command.LOCK
        assert message.getRouting() == MessageRouting.UNICAST
        assert message.getReplicationStatus() == ReplicationStatus.FAILED
        assert message.getFromId() == otherNodeId
        assert message.getToId() == nodeId
    }

    void unlockTestFile() {
        unlockFile(file)
    }

    void unlockFile(String filename) {
        // sequence is not checked now
        ReplicationMessage unlockRequest = createUnlockRequest(filename, owner, nodeId, 0)
        client.sendMessage(unlockRequest)
    }
}
