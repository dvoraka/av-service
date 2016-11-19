package dvoraka.avservice.checker

import dvoraka.avservice.checker.configuration.LoadTestConfig
import dvoraka.avservice.common.testing.DefaultLoadTestProperties
import dvoraka.avservice.common.testing.LoadTestProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Load tester test.
 */
@ContextConfiguration(classes = [LoadTestConfig.class])
class LoadTesterISpec extends Specification {

    @Autowired
    LoadTester tester


    def setup() {
        tester.getAvSender().purgeQueue("av-check")
    }

    def cleanup() {
        tester.getAvSender().purgeQueue("av-check")
    }

    def "run load test"() {
        given:
            LoadTestProperties props = new DefaultLoadTestProperties.Builder()
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
