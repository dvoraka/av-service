package dvoraka.avservice.client.transport.test

import dvoraka.avservice.common.helper.replication.ReplicationHelper
import spock.lang.Specification
import spock.lang.Subject

class DefaultSimpleBrokerSpec extends Specification implements ReplicationHelper {

    @Subject
    DefaultSimpleBroker broker


    def setup() {
        broker = new DefaultSimpleBroker()
    }

    def "send message"() {
        expect:
            broker.send("queue", createDiscoverRequest("node ID"))
    }

    def "register and send message"() {
        setup:
            broker.addMessageListener({ msg -> println msg }, "test")
        expect:
            broker.send("test", createDiscoverRequest("node ID"))
    }
}
