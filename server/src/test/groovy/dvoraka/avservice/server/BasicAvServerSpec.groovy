package dvoraka.avservice.server

import dvoraka.avservice.MessageProcessor
import dvoraka.avservice.client.ServerComponent
import dvoraka.avservice.db.service.MessageInfoService
import spock.lang.Specification
import spock.lang.Subject

/**
 * Basic AV server spec.
 */
class BasicAvServerSpec extends Specification {

    @Subject
    BasicAvServer server

    ServerComponent component
    MessageProcessor processor
    MessageInfoService messageInfoService


    def setup() {
        component = Mock()
        processor = Mock()
        messageInfoService = Mock()

        server = new BasicAvServer('TEST1', component, processor, messageInfoService)
    }

    def "default server status"() {
        expect:
            !server.isStarted()
            !server.isRunning()
            server.isStopped()
    }

    def "start server"() {
        when:
            server.start()

        then:
            server.isStarted()
    }

    def "after server started"() {
        when:
            server.start()

        then:
            server.isStarted()
            !server.isStopped()
    }

    def "stop server"() {
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
        when:
            server.start()

        then:
            server.isStarted()

        when:
            server.restart()

        then:
            server.isStarted()
    }
}
