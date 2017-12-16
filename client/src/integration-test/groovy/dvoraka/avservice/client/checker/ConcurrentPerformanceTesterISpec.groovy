package dvoraka.avservice.client.checker

import dvoraka.avservice.client.configuration.ClientConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'amqp', 'performance', 'file-client', 'no-db'])
@DirtiesContext
class ConcurrentPerformanceTesterISpec extends Specification {

    @Autowired
    ConcurrentPerformanceTester loadTester


    def setupSpec() {
        System.setProperty('avservice.perf.msgCount', '10')
    }

    def "run test"() {
        expect:
            loadTester.run()
    }
}
