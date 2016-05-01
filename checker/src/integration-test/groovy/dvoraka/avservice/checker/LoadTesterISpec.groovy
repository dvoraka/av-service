package dvoraka.avservice.checker

import spock.lang.Specification

/**
 * Run load test.
 */
class LoadTesterISpec extends Specification {


    def "run load test"() {
        setup:
        LoadTestProperties props = new BasicProperties.Builder()
                .msgCount(5)
                .virtualHost("antivirus")
                .destinationQueue("av-result")
                .appId("antivirus")
                .synchronous(false)
                .build()
        props.setSendOnly(true)

        Tester tester = new LoadTester(props)

        when:
        tester.startTest()

        then:
        notThrown(Exception)
    }
}
