package dvoraka.avservice.runner.service

import dvoraka.avservice.runner.DefaultRunnerConfiguration
import dvoraka.avservice.runner.RunnerConfiguration
import dvoraka.avservice.runner.RunningState
import dvoraka.avservice.runner.server.jms.JmsFileServerRunner
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

class DefaultRunnerServiceISpec extends Specification {

    @Subject
    DefaultRunnerService service

    @Shared
    String runnerName = 'jmsFileServerRunner'

    RunnerConfiguration configuration


    def setup() {
        service = new DefaultRunnerService()

        configuration = new DefaultRunnerConfiguration(
                runnerName,
                new JmsFileServerRunner(),
                { sleep(1_000); true }
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
