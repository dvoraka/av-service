package dvoraka.avservice.rest

import dvoraka.avservice.server.ServerComponent
import spock.lang.Specification
import spock.lang.Subject

/**
 * Remote REST strategy spec.
 */
class RemoteRestStrategySpec extends Specification {

    @Subject
    RemoteRestStrategy strategy

    ServerComponent serverComponent


    def setup() {
        serverComponent = Mock()
        strategy = new RemoteRestStrategy(serverComponent)
    }

    def cleanup() {
        strategy.stop()
    }

    def "start"() {
        when:
            strategy.start()

        then:
            notThrown(Exception)
            strategy.isStarted()
    }

    def "start and stop"() {
        when:
            strategy.start()

        then:
            notThrown(Exception)
            strategy.isStarted()

        when:
            strategy.stop()

        then:
            notThrown(Exception)
            !strategy.isStarted()
    }
}
