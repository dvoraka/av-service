package dvoraka.avservice.runner.service

import dvoraka.avservice.client.checker.Checker
import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.runner.DefaultRunnerConfiguration
import dvoraka.avservice.runner.RunnerConfiguration
import dvoraka.avservice.runner.RunningState
import dvoraka.avservice.runner.server.jms.JmsFileServerRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'file-client', 'jms', 'check', 'checker', 'no-db'])
@DirtiesContext
class DefaultRunnerServiceISpec extends Specification {

    @Subject
    DefaultRunnerService service

    @Autowired
    Checker checker

    @Shared
    String runnerName = 'jmsFileServerRunner'

    RunnerConfiguration configuration


    def setup() {
        service = new DefaultRunnerService()

        configuration = new DefaultRunnerConfiguration(
                runnerName,
                new JmsFileServerRunner(),
                { checker.check() }
        )
    }

    def "add configuration"() {
        expect:
            service.createRunner(configuration) == runnerName
            service.getRunnerCount() == 1
            service.getRunnerState(runnerName) == RunningState.NEW
    }

    def "add and run configuration"() {
        when:
            service.createRunner(configuration)

        then:
            service.getRunnerCount() == 1
            service.getRunnerState(runnerName) == RunningState.NEW

        when:
            service.startRunner(runnerName)

        then:
            service.getRunnerState(runnerName) == RunningState.STARTING

        when:
            service.waitForStart(runnerName)

        then:
            service.getRunnerState(runnerName) == RunningState.RUNNING
    }
}
