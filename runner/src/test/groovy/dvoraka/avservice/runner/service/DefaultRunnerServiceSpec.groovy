package dvoraka.avservice.runner.service

import dvoraka.avservice.runner.RunnerNotFoundException
import spock.lang.Specification
import spock.lang.Subject

class DefaultRunnerServiceSpec extends Specification {

    @Subject
    DefaultRunnerService service


    def setup() {
        service = new DefaultRunnerService()
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
