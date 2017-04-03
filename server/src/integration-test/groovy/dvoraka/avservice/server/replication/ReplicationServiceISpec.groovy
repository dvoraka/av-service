package dvoraka.avservice.server.replication

import dvoraka.avservice.client.ReplicationComponent
import dvoraka.avservice.client.amqp.AmqpReplicationComponent
import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.client.service.DefaultReplicationServiceClient
import dvoraka.avservice.client.service.ReplicationServiceClient
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.runner.ServiceRunner
import dvoraka.avservice.server.runner.amqp.AmqpReplicationServiceRunner
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

/**
 * Replication service spec.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'amqp', 'amqp-client', 'no-db'])
@Ignore('WIP')
class ReplicationServiceISpec extends Specification {

    @Autowired
    RabbitTemplate rabbitTemplate

    ReplicationComponent component
    ReplicationServiceClient client

    @Shared
    ServiceRunner runner


    def setup() {
        component = new AmqpReplicationComponent(rabbitTemplate)
        client = new DefaultReplicationServiceClient(component, "testNode")
    }

    def setupSpec() {
        runner = new AmqpReplicationServiceRunner()
        runner.runAsync()
    }

    def cleanupSpec() {
        runner.stop()
    }

    def "test"() {
        expect:
            true
    }

    @Ignore
    def "save file"() {
        expect:
            service.saveFile(Utils.genSaveMessage())
    }
}
