package dvoraka.avservice.server.replication

import dvoraka.avservice.client.service.ReplicationServiceClient
import dvoraka.avservice.client.service.response.ReplicationMessageList
import dvoraka.avservice.client.service.response.ReplicationResponseClient
import dvoraka.avservice.common.data.Command
import dvoraka.avservice.common.data.MessageRouting
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.common.data.ReplicationMessage
import dvoraka.avservice.common.data.ReplicationStatus
import dvoraka.avservice.common.helper.FileServiceHelper
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
 * Remote lock spec. Other specs are in Replication service spec.
 * <p>
 * It uses low-level clients for simulating a service node. A real service node runs on
 * the replication network and is started automatically before the testing.
 */
@Stepwise
@ContextConfiguration(classes = [StorageConfig.class])
@ActiveProfiles(['storage', 'replication-test', 'client', 'amqp', 'db-mem'])
@PropertySource('classpath:avservice.properties')
@DirtiesContext
class RemoteLockISpec extends Specification
        implements ReplicationHelper, FileServiceHelper {

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
        sleep(2_000)
    }


    def "lock request twice for same file"() {
        given:
            ReplicationMessage request = createLockRequest(file, owner, nodeId, 1)
            ReplicationMessage request2 = createLockRequest(file, owner, nodeId, 2)

        when:
            client.sendMessage(request)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWait(request.getId(), responseTime)

        then:
            messages.isPresent()
            messages.get().stream().count() == 1

        and:
            ReplicationMessage response = messages.get().stream().findAny().get()
            checkOkResponse(response)
            response.getSequence() == request.getSequence()

        when:
            client.sendMessage(request2)
            messages = responseClient.getResponseWait(request2.getId(), responseTime)

        then:
            messages.isPresent()
            messages.get().stream().count() == 1

        and:
            ReplicationMessage response2 = messages.get().stream().findAny().get()
            checkFailedResponse(response2)
            response2.getSequence() == request2.getSequence()

        cleanup:
            unlockTestFile()
    }

    def "two lock requests for same file at once"() {
        given:
            ReplicationMessage request = createLockRequest(file, owner, nodeId, 2)
            ReplicationMessage request2 = createLockRequest(file, owner, nodeId, 2)

        when:
            client.sendMessage(request)
            client.sendMessage(request2)

            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWait(request.getId(), responseTime)
            Optional<ReplicationMessageList> messages2 =
                    responseClient.getResponseWait(request.getId(), responseTime)

        then:
            messages.isPresent()
            messages.get().stream().count() == 1
            messages2.isPresent()
            messages2.get().stream().count() == 1

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
            ReplicationMessage request = createLockRequest(file, owner, nodeId, 3)
            ReplicationMessage request2 = createLockRequest(file, owner, nodeId, 5)

        when:
            client.sendMessage(request)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWait(request.getId(), responseTime)

        then:
            messages.isPresent()
            messages.get().stream().count() == 1

        and:
            ReplicationMessage response = messages.get().stream().findAny().get()
            checkOkResponse(response)
            response.getSequence() == request.getSequence()

        when:
            client.sendMessage(request2)
            messages = responseClient.getResponseWait(request2.getId(), responseTime)

        then:
            messages.isPresent()
            messages.get().stream().count() == 1

        and:
            ReplicationMessage response2 = messages.get().stream().findAny().get()
            checkFailedResponse(response2)
            response2.getSequence() == request.getSequence() + 1

        cleanup:
            unlockTestFile()
    }

    def "lock two different files"() {
        given:
            ReplicationMessage request = createLockRequest(file, owner, nodeId, 4)
            String otherFile = file + "2"
            ReplicationMessage request2 = createLockRequest(otherFile, owner, nodeId, 5)

        when:
            client.sendMessage(request)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWait(request.getId(), responseTime)

        then:
            messages.isPresent()
            messages.get().stream().count() == 1

        and:
            ReplicationMessage response = messages.get().stream().findAny().get()
            checkOkResponse(response)
            response.getSequence() == request.getSequence()

        when:
            client.sendMessage(request2)
            messages = responseClient.getResponseWait(request2.getId(), responseTime)

        then:
            messages.isPresent()
            messages.get().stream().count() == 1

        and:
            ReplicationMessage response2 = messages.get().stream().findAny().get()
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
