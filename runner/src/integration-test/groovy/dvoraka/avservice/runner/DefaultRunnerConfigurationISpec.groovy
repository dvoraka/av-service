package dvoraka.avservice.runner

import dvoraka.avservice.client.checker.Checker
import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.common.runner.ServiceRunner
import dvoraka.avservice.runner.server.amqp.AmqpCheckServerRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

import java.util.function.BooleanSupplier

@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'file-client', 'amqp', 'check', 'checker', 'no-db'])
@DirtiesContext
@Ignore("manual testing")
class DefaultRunnerConfigurationISpec extends Specification {

    @Autowired
    Checker checker


    def "test"() {
        expect:
            true
    }

    def "build and run configuration"() {
        given:
            String configurationId = 'test'
            ServiceRunner serviceRunner = new AmqpCheckServerRunner()
            BooleanSupplier supplier = checker

            DefaultRunnerConfiguration configuration = new DefaultRunnerConfiguration(
                    configurationId,
                    serviceRunner,
                    supplier
            )

        expect:
            !configuration.running().getAsBoolean()

        when:
            configuration.getServiceRunner().runAsync()
            sleep(5_000)

        then:
            configuration.running().getAsBoolean()

        cleanup:
            configuration.getServiceRunner().stop()
    }
}
