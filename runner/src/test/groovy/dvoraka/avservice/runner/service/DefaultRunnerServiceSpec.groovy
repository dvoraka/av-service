package dvoraka.avservice.runner.service

import dvoraka.avservice.runner.DummyRunnerConfiguration
import dvoraka.avservice.runner.RunnerConfiguration
import dvoraka.avservice.runner.RunnerNotFoundException
import dvoraka.avservice.runner.RunningState
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

@Ignore("WIP")
class DefaultRunnerServiceSpec extends Specification {

    @Subject
    DefaultRunnerService service

    RunnerConfiguration configuration

    @Shared
    String unknownName = "aaaaaa"


    def setup() {
        configuration = new DummyRunnerConfiguration()
        service = new DefaultRunnerService()
    }

    def cleanup() {
        service.stop()
    }

    def "create runner"() {
        when:
            String name = service.createRunner(configuration)

        then:
            service.getRunnerState(name) == RunningState.NEW
    }

    def "list configurations"() {
        when:
            service.createRunner(configuration)

        then:
            service.listRunners().size() == 1
            service.listRunners().stream().findAny().orElse("") == configuration.getName()
    }

    def "create runner and start"() {
        when:
            String name = service.createRunner(configuration)

        then:
            service.getRunnerState(name) == RunningState.NEW

        when:
            service.startRunner(configuration.getName())
            sleep(100)

        then:
            service.getRunnerState(name) == RunningState.RUNNING
    }

    def "create runner, start and stop"() {
        when:
            String name = service.createRunner(configuration)

        then:
            service.getRunnerState(name) == RunningState.NEW

        when:
            service.startRunner(name)
            sleep(100)

        then:
            service.getRunnerState(name) == RunningState.RUNNING

        when:
            service.stopRunner(name)
            sleep(100)

        then:
            service.getRunnerState(name) == RunningState.STOPPED
    }

    def "start with unknown name"() {
        when:
            service.startRunner(unknownName)

        then:
            thrown(RunnerNotFoundException)
    }

    def "stop with unknown name"() {
        when:
            service.stopRunner(unknownName)

        then:
            thrown(RunnerNotFoundException)
    }

    def "state for unknown name"() {
        when:
            service.stopRunner(unknownName)

        then:
            thrown(RunnerNotFoundException)
    }
}
