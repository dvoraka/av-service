package dvoraka.avservice.runner.service

import dvoraka.avservice.runner.DummyRunnerConfiguration
import dvoraka.avservice.runner.RunnerConfiguration
import dvoraka.avservice.runner.RunnerNotFoundException
import dvoraka.avservice.runner.RunningState
import spock.lang.Specification
import spock.lang.Subject

class DefaultRunnerServiceSpec extends Specification {

    @Subject
    DefaultRunnerService service

    RunnerConfiguration configuration


    def setup() {
        configuration = new DummyRunnerConfiguration()
        service = new DefaultRunnerService()
    }

    def "create runner"() {
        when:
            service.create(configuration)

        then:
            service.getState(configuration.getId()) == RunningState.UNKNOWN
    }

    def "create runner and start"() {
        when:
            service.create(configuration)

        then:
            service.getState(configuration.getId()) == RunningState.UNKNOWN

        when:
            service.start(configuration.getId())
            sleep(100)

        then:
            service.getState(configuration.getId()) == RunningState.RUNNING
    }

    def "start with unknown ID"() {
        when:
            service.start("aaaaaa")

        then:
            thrown(RunnerNotFoundException)
    }

    def "stop with unknown ID"() {
        when:
            service.stop("aaaaaa")

        then:
            thrown(RunnerNotFoundException)
    }

    def "state for unknown ID"() {
        when:
            service.stop("aaaaaa")

        then:
            thrown(RunnerNotFoundException)
    }
}
