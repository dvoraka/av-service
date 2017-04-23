package dvoraka.avservice.common.service

import spock.lang.Specification
import spock.lang.Subject

/**
 * Basic service management spec.
 */
class BasicServiceManagementSpec extends Specification {

    @Subject
    BasicServiceManagement app


    def setup() {
        app = new BasicServiceManagement()
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
