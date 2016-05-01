package dvoraka.avservice.checker

import spock.lang.Ignore
import spock.lang.Specification

/**
 * Run load test.
 */
@Ignore
class LoadTesterISpec extends Specification {


    def "run load test"() {
        setup:
        LoadTestProperties props = new BasicProperties()
        Tester tester = new LoadTester(props)

        when:
        tester.startTest()

        then:
        notThrown(Exception)
    }
}
