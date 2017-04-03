package dvoraka.avservice.server.replication

import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.client.service.ReplicationServiceClient
import dvoraka.avservice.client.service.response.ReplicationMessageList
import dvoraka.avservice.client.service.response.ReplicationResponseClient
import dvoraka.avservice.common.data.ReplicationMessage
import dvoraka.avservice.common.runner.ServiceRunner
import dvoraka.avservice.server.runner.amqp.AmqpReplicationServiceRunner
import dvoraka.avservice.storage.replication.ReplicationHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
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
class ReplicationServiceISpec extends Specification implements ReplicationHelper {

    @Autowired
    ReplicationServiceClient client
    @Autowired
    ReplicationResponseClient responseClient

    @Value('${avservice.storage.replication.testNodeId}')
    String nodeId

    long responseTime = 2_000

    @Shared
    ServiceRunner runner


    def setup() {
    }

    def setupSpec() {
        runner = new AmqpReplicationServiceRunner()
        runner.runAsync()
        sleep(7_000)
    }

    def cleanupSpec() {
        runner.stop()
    }

    def "discovery testing"() {
        given:
            ReplicationMessage request = createDiscoverRequest(nodeId)

        when:
            client.sendMessage(request)
            Optional<ReplicationMessageList> messages =
                    responseClient.getResponseWait(request.getId(), responseTime)

        then:
            messages.isPresent()
            messages.get().stream().count() == 1
    }
}
