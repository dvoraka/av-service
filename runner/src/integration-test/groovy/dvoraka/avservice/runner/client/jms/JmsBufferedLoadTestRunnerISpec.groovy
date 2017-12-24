package dvoraka.avservice.runner.client.jms

import spock.lang.Specification

class JmsBufferedLoadTestRunnerISpec extends Specification {

    def setupSpec() {
        System.setProperty('avservice.perf.msgCount', '2')
    }

    def "Run JMS buffered load test runner"() {
        when:
            JmsBufferedLoadTestRunner.main([] as String[])

        then:
            notThrown(Exception)
    }
}
