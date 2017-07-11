package dvoraka.avservice.server

import dvoraka.avservice.client.NetworkComponent
import spock.lang.Specification
import spock.lang.Subject

/**
 * Server component bridge spec.
 */
class NetworkComponentBridgeSpec extends Specification {

    @Subject
    ServerComponentBridge bridge

    NetworkComponent inComponent
    NetworkComponent outComponent


    def setup() {
        inComponent = Mock()
        outComponent = Mock()

        bridge = new ServerComponentBridge(inComponent, outComponent)
    }

    def "constructor with same components"() {
        given:
            inComponent = Mock()
            outComponent = inComponent

        when:
            new ServerComponentBridge(inComponent, outComponent)

        then:
            thrown(IllegalArgumentException)
    }

    def "constructor with nulls"() {
        when:
            new ServerComponentBridge(null, null)

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

            1 * inComponent.addAvMessageListener(_)
            1 * outComponent.addAvMessageListener(_)

        when:
            bridge.stop()

        then:
            !bridge.isRunning()
            !bridge.isStarted()
            bridge.isStopped()

            1 * inComponent.removeAvMessageListener(_)
            1 * outComponent.removeAvMessageListener(_)
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

            1 * inComponent.addAvMessageListener(_)
            1 * outComponent.addAvMessageListener(_)

        when:
            bridge.restart()

        then:
            bridge.isRunning()
            bridge.isStarted()
            !bridge.isStopped()

            1 * inComponent.removeAvMessageListener(_)
            1 * outComponent.removeAvMessageListener(_)
            1 * inComponent.addAvMessageListener(_)
            1 * outComponent.addAvMessageListener(_)
    }
}
