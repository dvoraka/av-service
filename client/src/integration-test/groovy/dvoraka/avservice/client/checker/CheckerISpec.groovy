package dvoraka.avservice.client.checker

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.exception.MessageNotFoundException
import dvoraka.avservice.runner.RunnerConfiguration
import dvoraka.avservice.runner.RunnerConfigurationHelper
import dvoraka.avservice.runner.service.RunnerService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

/**
 * Checker spec base.
 */
@Ignore('base class')
class CheckerISpec extends Specification implements RunnerConfigurationHelper {

    @Autowired
    Checker checker

    @Autowired
    RunnerService runnerService

    @Shared
    RunnerConfiguration runnerConfiguration


    def setupSpec() {
        System.setProperty('itest', 'itest')

        // initialize runner configuration
    }

    def cleanupSpec() {
    }

    def setup() {
        runnerConfiguration.updateChecker(checker)

        if (!runnerService.exists(runnerConfiguration.getName())) {
            runnerService.createRunner(runnerConfiguration)
        }

        runnerService.startRunner(runnerConfiguration.getName())
        runnerService.waitForStart(runnerConfiguration.getName())
    }

    def "send and receive normal message"() {
        given:
            AvMessage message = Utils.genMessage()

        when:
            checker.sendAvMessage(message)
            AvMessage receivedMessage = checker.receiveMessage(message.getId())

        then:
            notThrown(Exception)
            receivedMessage.virusInfo == Utils.OK_VIRUS_INFO
    }

    def "send and receive infected message"() {
        given:
            AvMessage message = Utils.genInfectedMessage()

        when:
            checker.sendAvMessage(message)
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
            checker.sendAvMessage(message)
            count.times {
                checker.sendAvMessage(Utils.genMessage())
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
