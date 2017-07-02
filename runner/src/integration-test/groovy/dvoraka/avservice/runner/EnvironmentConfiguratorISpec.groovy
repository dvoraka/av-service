package dvoraka.avservice.runner

import spock.lang.Specification

/**
 * Test for configurator runner.
 */
class EnvironmentConfiguratorISpec extends Specification {

    def "run configurator"() {
        when:
            EnvironmentConfigurator.main([] as String[])

        then:
            notThrown(Exception)
    }
}
