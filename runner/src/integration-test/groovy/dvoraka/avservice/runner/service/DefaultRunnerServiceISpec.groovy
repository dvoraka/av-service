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
import spock.lang.Stepwise
import spock.lang.Subject

@Stepwise
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'file-client', 'jms', 'checker', 'no-db'])
@DirtiesContext
class DefaultRunnerServiceISpec extends Specification {

    @Subject
    @Shared
    DefaultRunnerService service = new DefaultRunnerService()

    @Autowired
    Checker checker

    @Shared
    String runnerName = 'jmsFileServerRunner'

    RunnerConfiguration configuration = new DefaultRunnerConfiguration(
            runnerName,
            new JmsFileServerRunner(),
            { checker.check() }
    )


    def cleanupSpec() {
        service.stop()
    }

    def "add configuration"() {
        expect:
            service.createRunner(configuration) == runnerName
            service.getRunnerCount() == 1
            service.getRunnerState(runnerName) == RunningState.NEW
    }

    def "run configuration"() {
        when:
            service.startRunner(runnerName)

        then:
            service.getRunnerState(runnerName) == RunningState.STARTING

        when:
            service.waitForStart(runnerName)

        then:
            service.getRunnerState(runnerName) == RunningState.RUNNING
    }

    def "start runner and run check"() {
        when:
            service.startRunner(runnerName)
            service.waitForStart(runnerName)

        then:
            checker.check()
    }
}
