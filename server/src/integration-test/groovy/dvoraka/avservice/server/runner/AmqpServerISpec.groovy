package dvoraka.avservice.server.runner

import spock.lang.Specification

/**
 * Test for server running.
 */
class AmqpServerISpec extends Specification {

    def "Run AMQP server"() {
        when:
            AmqpServer.setTestRun(true)
            AmqpServer.main([] as String[])

        then:
            notThrown(Exception)
    }
}
