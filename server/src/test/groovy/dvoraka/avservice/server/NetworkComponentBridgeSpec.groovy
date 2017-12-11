package dvoraka.avservice.server

import dvoraka.avservice.client.transport.AvNetworkComponent
import spock.lang.Specification
import spock.lang.Subject

class NetworkComponentBridgeSpec extends Specification {

    @Subject
    AvNetworkComponentBridge bridge

    AvNetworkComponent inComponent
    AvNetworkComponent outComponent


    def setup() {
        inComponent = Mock()
        outComponent = Mock()

        bridge = new AvNetworkComponentBridge(inComponent, outComponent)
    }

    def "constructor with same components"() {
        given:
            inComponent = Mock()
            outComponent = inComponent

        when:
            new AvNetworkComponentBridge(inComponent, outComponent)

        then:
            thrown(IllegalArgumentException)
    }

    def "constructor with nulls"() {
        when:
            new AvNetworkComponentBridge(null, null)

        then:
            thrown(NullPointerException)
    }

    def "start and stop"() {
        expect:
            !bridge.isRunning()
            !bridge.isStarted()
            !bridge.isStopped()

        when:
            bridge.start()

        then:
            bridge.isRunning()
            bridge.isStarted()
            !bridge.isStopped()

            1 * inComponent.addMessageListener(_)
            1 * outComponent.addMessageListener(_)

        when:
            bridge.stop()

        then:
            !bridge.isRunning()
            !bridge.isStarted()
            bridge.isStopped()

            1 * inComponent.removeMessageListener(_)
            1 * outComponent.removeMessageListener(_)
    }

    def "start and start"() {
        when:
            bridge.start()
            bridge.start()

        then:
            bridge.isRunning()
    }

    def "stop and stop"() {
        when:
            bridge.stop()
            bridge.stop()

        then:
            !bridge.isRunning()
    }

    def "restart"() {
        when:
            bridge.start()

        then:
            bridge.isRunning()
            bridge.isStarted()
            !bridge.isStopped()

            1 * inComponent.addMessageListener(_)
            1 * outComponent.addMessageListener(_)

        when:
            bridge.restart()

        then:
            bridge.isRunning()
            bridge.isStarted()
            !bridge.isStopped()

            1 * inComponent.removeMessageListener(_)
            1 * outComponent.removeMessageListener(_)
            1 * inComponent.addMessageListener(_)
            1 * outComponent.addMessageListener(_)
    }
}
