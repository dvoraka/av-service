package dvoraka.avservice.server

import spock.lang.Specification

/**
 * Test configurator.
 */
class EnvironmentConfiguratorISpec extends Specification {

    def "run configurator"() {
        when:
        EnvironmentConfigurator.main([] as String[])

        then:
        notThrown(Exception)
    }
}
