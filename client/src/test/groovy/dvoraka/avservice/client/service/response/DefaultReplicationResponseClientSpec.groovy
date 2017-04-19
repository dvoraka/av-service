package dvoraka.avservice.client.service.response

import dvoraka.avservice.client.ReplicationComponent
import spock.lang.Specification
import spock.lang.Subject

/**
 * DefaultReplicationResponseClient spec.
 */
class DefaultReplicationResponseClientSpec extends Specification {

    @Subject
    DefaultReplicationResponseClient client

    ReplicationComponent replicationComponent
    String nodeId = 'testId'


    def setup() {
        replicationComponent = Mock()

        client = new DefaultReplicationResponseClient(replicationComponent, nodeId)
    }

    def "start and stop"() {
        when:
            client.start()

        then:
            client.isStarted()

        when: "start again"
            client.start()

        then:
            client.isStarted()

        when:
            client.stop()

        then:
            !client.isStarted()

        when: "stop again"
            client.stop()

        then:
            !client.isStarted()
    }
}
