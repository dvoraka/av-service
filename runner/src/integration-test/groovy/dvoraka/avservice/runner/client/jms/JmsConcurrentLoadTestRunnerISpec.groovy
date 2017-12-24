package dvoraka.avservice.runner.client.jms

import spock.lang.Specification

class JmsConcurrentLoadTestRunnerISpec extends Specification {

    def setupSpec() {
        System.setProperty('avservice.perf.msgCount', '2')
    }

    def "Run JMS concurrent load test runner"() {
        when:
            JmsConcurrentLoadTestRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
