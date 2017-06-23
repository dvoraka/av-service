package dvoraka.avservice.client.checker

import dvoraka.avservice.client.service.AvServiceClient
import dvoraka.avservice.client.service.response.ResponseClient
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.testing.PerformanceTestProperties
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

    def "start"() {
        when:
            tester.start()

        then:
            1 * testProperties.getMsgCount() >> 1

            1 * avServiceClient.checkMessage(_)
            1 * responseClient.getResponse(_) >> Utils.genInfectedMessage()
                    .createCheckResponse("")
    }

    def "is not running before start"() {
        expect:
            !tester.isRunning()
    }

    def "is not done before start"() {
        expect:
            !tester.isDone()
    }
}
