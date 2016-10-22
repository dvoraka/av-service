package dvoraka.avservice.server.runner

import spock.lang.Specification

/**
 * Test for server running.
 */
class JmsServerISpec extends Specification {

    def "Run JMS server"() {
        when:
            JmsServer.setTestRun(true)
            JmsServer.main([] as String[])

        then:
            notThrown(Exception)
    }
}
