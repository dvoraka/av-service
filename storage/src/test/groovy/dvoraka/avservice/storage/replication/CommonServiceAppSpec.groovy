package dvoraka.avservice.storage.replication

import spock.lang.Specification
import spock.lang.Subject

/**
 * Replication service app spec.
 */
class CommonServiceAppSpec extends Specification {

    @Subject
    CommonServiceApp app


    def setup() {
        app = new CommonServiceApp()
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
