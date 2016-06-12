package dvoraka.avservice.server

import dvoraka.avservice.MessageProcessor
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification

/**
 * Basic AV server spec.
 */
class BasicAvServerSpec extends Specification {

    BasicAvServer server


    def setup() {
        server = new BasicAvServer()
    }

    def "default server status"() {
        expect:
        !server.isStarted()
        !server.isRunning()
        server.isStopped()
    }

    def "start server"() {
        given:
        ServerComponent component = Mock()
        MessageProcessor processor = Mock()

        setScMpMocks(component, processor)

        when:
        server.start()

        then:
        server.isStarted()
    }

    def "stop server"() {
        given:
        ServerComponent component = Mock()
        MessageProcessor processor = Mock()

        setScMpMocks(component, processor)

        when:
        server.start()

        then:
        server.isStarted()

        when:
        server.stop()

        then:
        server.isStopped()
    }

    def "restart server"() {
        given:
        ServerComponent component = Mock()
        MessageProcessor processor = Mock()

        setScMpMocks(component, processor)

        when:
        server.start()

        then:
        server.isStarted()

        when:
        server.restart()

        then:
        server.isStarted()
    }

    void setScMpMocks(ServerComponent component, MessageProcessor processor) {
        ReflectionTestUtils.setField(server, null, component, ServerComponent.class)
        ReflectionTestUtils.setField(server, null, processor, MessageProcessor.class)
    }
}
