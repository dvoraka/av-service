package dvoraka.avservice.client.service.response

import dvoraka.avservice.common.helper.replication.ReplicationHelper
import spock.lang.Specification
import spock.lang.Subject

/**
 * List spec.
 */
class ReplicationMessageListSpec extends Specification implements ReplicationHelper {

    @Subject
    ReplicationMessageList messages


    def setup() {
        messages = new ReplicationMessageList()
    }

    def "add message"() {
        expect:
            messages.size() == 0

        when:
            messages.add(createDiscoveryRequest('node ID'))

        then:
            messages.size() == 1
    }

    def "get stream"() {
        expect:
            messages.size() == 0

        when:
            messages.add(createDiscoveryRequest('node ID'))
            messages.add(createDiscoveryRequest('node ID'))

        then:
            messages.stream().count() == 2
    }
}
