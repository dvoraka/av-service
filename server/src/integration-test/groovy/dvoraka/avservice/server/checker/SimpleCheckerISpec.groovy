package dvoraka.avservice.server.checker

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.exception.MessageNotFoundException
import dvoraka.avservice.common.runner.Runner
import dvoraka.avservice.server.configuration.amqp.AmqpConfig
import dvoraka.avservice.server.runner.AmqpServerRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

/**
 * Simple checker spec.
 */
@ContextConfiguration(classes = [AmqpConfig.class])
@ActiveProfiles(['core', 'amqp', 'amqp-checker', 'no-db'])
class SimpleCheckerISpec extends Specification {

    @Autowired
    Checker checker

    @Shared
    Runner runner


    def setupSpec() {
        AmqpServerRunner.setTestRun(false)
        runner = new AmqpServerRunner()
        runner.runAsync()
    }

    def cleanupSpec() {
        runner.stop()
    }

    def setup() {
        while (!runner.isRunning()) {
            sleep(200)
        }
    }

    def "send and receive normal message"() {
        given:
            AvMessage message = Utils.genNormalMessage()

        when:
            checker.sendMessage(message)
            AvMessage receivedMessage = checker.receiveMessage(message.getId())

        then:
            notThrown(Exception)
            receivedMessage.virusInfo == Utils.OK_VIRUS_INFO
    }

    def "send and receive infected message"() {
        given:
            AvMessage message = Utils.genInfectedMessage()

        when:
            checker.sendMessage(message)
            AvMessage receivedMessage = checker.receiveMessage(message.getId())

        then:
            notThrown(Exception)
            receivedMessage.virusInfo != Utils.OK_VIRUS_INFO
    }

    def "send few normal messages after infected one and receive the first one"() {
        given:
            int count = 5
            AvMessage message = Utils.genInfectedMessage()

        when:
            checker.sendMessage(message)
            count.times {
                checker.sendMessage(Utils.genNormalMessage())
            }
            AvMessage resultMessage = checker.receiveMessage(message.getId())

        then:
            resultMessage.getVirusInfo() != Utils.OK_VIRUS_INFO
    }

    def "try to receive nonexistent message"() {
        when:
            checker.receiveMessage("XXX")

        then:
            thrown(MessageNotFoundException)
    }

    def "check method testing"() {
        expect:
            checker.check()
    }
}