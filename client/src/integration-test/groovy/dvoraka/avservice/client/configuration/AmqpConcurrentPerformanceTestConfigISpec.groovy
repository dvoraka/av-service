package dvoraka.avservice.client.configuration

import dvoraka.avservice.client.checker.ConcurrentPerformanceTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'amqp', 'performance', 'file-client', 'no-db'])
@DirtiesContext
class AmqpConcurrentPerformanceTestConfigISpec extends Specification {

    @Autowired
    ConcurrentPerformanceTester loadTester


    def "test"() {
        expect:
            true
    }
}
