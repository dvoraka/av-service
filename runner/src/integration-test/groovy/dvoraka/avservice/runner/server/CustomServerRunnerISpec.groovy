package dvoraka.avservice.runner.server

import spock.lang.Specification

/**
 * Test for server runner.
 */
class CustomServerRunnerISpec extends Specification {

    def "Run custom server runner"() {
        when:
            CustomServerRunner.setTestRun(true)
            CustomServerRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
