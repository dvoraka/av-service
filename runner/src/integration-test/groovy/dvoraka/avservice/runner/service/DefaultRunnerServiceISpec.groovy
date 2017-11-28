package dvoraka.avservice.runner.service

import dvoraka.avservice.client.checker.Checker
import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.runner.RunnerConfigurationHelper
import dvoraka.avservice.runner.RunningState
import dvoraka.avservice.runner.configuration.RunnerConfig
import dvoraka.avservice.runner.runnerconfiguration.RunnerConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Subject

@Stepwise
@ContextConfiguration(classes = [ClientConfig.class, RunnerConfig.class])
@ActiveProfiles(['client', 'file-client', 'jms', 'checker', 'no-db'])
@DirtiesContext
class DefaultRunnerServiceISpec extends Specification implements RunnerConfigurationHelper {

    @Subject
    @Autowired
    RunnerService service

    @Autowired
    Checker checker

    @Shared
    RunnerConfiguration configuration = jmsFileServerConfiguration()
    @Shared
    String runnerName = configuration.getName()


    def "add configuration"() {
        setup:
            configuration.updateChecker(checker)

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
