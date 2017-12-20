package dvoraka.avservice.client.perf

import dvoraka.avservice.client.configuration.ClientConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'amqp', 'performance', 'file-client', 'no-db'])
@DirtiesContext
@Ignore("needs running server")
class ConcurrentPerformanceTesterISpec extends Specification {

    @Autowired
    ConcurrentPerformanceTester loadTester


    def setupSpec() {
        System.setProperty('avservice.perf.msgCount', '10000')
        System.setProperty('avservice.perf.threadCount', '4')
    }

    def "run test"() {
        expect:
            loadTester.run()
    }
}
