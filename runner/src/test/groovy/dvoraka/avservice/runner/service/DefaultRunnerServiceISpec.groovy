package dvoraka.avservice.runner.service

import dvoraka.avservice.runner.DefaultRunnerConfiguration
import dvoraka.avservice.runner.RunnerConfiguration
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
                { return true }
        )
    }

    def "Add configuration"() {
        expect:
            service.createRunner(configuration) == runnerName
            service.getRunnerCount() == 1
    }
}
