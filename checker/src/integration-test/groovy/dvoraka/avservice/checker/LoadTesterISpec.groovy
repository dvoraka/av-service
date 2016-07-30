package dvoraka.avservice.checker

import dvoraka.avservice.checker.configuration.LoadTestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Run load test.
 */
@ContextConfiguration(classes = [LoadTestConfig.class])
class LoadTesterISpec extends Specification {

    @Autowired
    LoadTester tester


    def "run load test"() {
        given:
            LoadTestProperties props = new BasicLoadTestProperties.Builder()
                    .msgCount(2)
                    .virtualHost("antivirus")
                    .destinationQueue("av-result")
                    .appId("antivirus")
                    .synchronous(false)
                    .build()
            props.setSendOnly(true)

            tester.setProps(props)

        when:
            tester.startTest()

        then:
            notThrown(Exception)
    }

    def "main method call"() {
        when:
            LoadTester.main([] as String[])

        then:
            notThrown(Exception)
    }
}
