package dvoraka.avservice.client.configuration

import dvoraka.avservice.client.checker.ConcurrentPerformanceTester
import dvoraka.avservice.common.testing.PerformanceTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'amqp', 'performance', 'concurrent', 'file-client', 'no-db'])
@DirtiesContext
class AmqpConcurrentPerformanceTestConfigISpec extends Specification {

    @Autowired
    PerformanceTest tester


    def "test"() {
        expect:
            tester instanceof ConcurrentPerformanceTester
    }
}
