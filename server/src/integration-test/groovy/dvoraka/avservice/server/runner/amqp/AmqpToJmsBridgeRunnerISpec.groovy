package dvoraka.avservice.server.runner.amqp

import spock.lang.Ignore
import spock.lang.Specification

/**
 * Test for bridge running.
 */
@Ignore
class AmqpToJmsBridgeRunnerISpec extends Specification {

    def "Run AMQP to JMS bridge"() {
        when:
            AmqpToJmsBridgeRunner.setTestRun(true)
            AmqpToJmsBridgeRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}