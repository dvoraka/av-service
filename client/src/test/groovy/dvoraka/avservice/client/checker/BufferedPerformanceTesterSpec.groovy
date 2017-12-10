package dvoraka.avservice.client.checker

import dvoraka.avservice.client.service.AvServiceClient
import dvoraka.avservice.client.service.response.ResponseClient
import dvoraka.avservice.common.testing.PerformanceTestProperties
import dvoraka.avservice.common.util.Utils
import spock.lang.Specification
import spock.lang.Subject

/**
 * Performance test spec.
 */
class BufferedPerformanceTesterSpec extends Specification {

    @Subject
    BufferedPerformanceTester tester

    AvServiceClient avServiceClient;
    ResponseClient responseClient;
    PerformanceTestProperties testProperties;


    def setup() {
        avServiceClient = Mock()
        responseClient = Mock()
        testProperties = Mock()

        tester = new BufferedPerformanceTester(avServiceClient, responseClient, testProperties)
    }

    def "run"() {
        when:
            tester.run()

        then:
            1 * testProperties.getMsgCount() >> 1

            1 * avServiceClient.checkMessage(_)
            1 * responseClient.getResponse(_) >> Utils.genInfectedMessage()
                    .createCheckResponse("")
        then:
            tester.getResult()
            tester.passed()
    }

    def "is not running before start"() {
        expect:
            !tester.isRunning()
    }

    def "is not done before start"() {
        expect:
            !tester.isDone()
    }

    def "setting timeout"() {
        given:
            int timeout = 1

        when:
            tester.setTimeout(1)

        then:
            tester.getTimeout() == timeout
    }
}
