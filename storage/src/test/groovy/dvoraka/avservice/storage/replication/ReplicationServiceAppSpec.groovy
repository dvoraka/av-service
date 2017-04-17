package dvoraka.avservice.storage.replication

import spock.lang.Specification
import spock.lang.Subject

/**
 * Replication service app spec.
 */
class ReplicationServiceAppSpec extends Specification {

    @Subject
    ReplicationServiceApp app

    ReplicationService service


    def setup() {
        service = Mock()
        app = new ReplicationServiceApp(service)
    }

    def "before start"() {
        expect:
            !app.isRunning()
            !app.isStarted()
            !app.isStopped()
    }

    def "after start"() {
        when:
            app.start()

        then:
            app.isRunning()
            app.isStarted()
    }

    def "start and stop"() {
        when:
            app.start()

        then:
            app.isRunning()
            app.isStarted()
            !app.isStopped()

        when:
            app.stop()

        then:
            !app.isRunning()
            app.isStopped()
    }
}
