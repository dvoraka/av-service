package dvoraka.avservice.rest.service

import dvoraka.avservice.common.runner.ServiceRunner
import dvoraka.avservice.rest.Application
import dvoraka.avservice.server.runner.amqp.AmqpFileServerRunner
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Shared

/**
 * REST with remote AMQP connections.
 */
@SpringBootTest(
        classes = [
                Application.class
        ],
        properties = [
                'spring.profiles.active=client,rest,rest-amqp,amqp,amqp-client,storage,db',
                'server.contextPath=/av-service',
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DirtiesContext
class RestServiceRemoteAmqpISpec extends RestServiceISpec {

    @Shared
    ServiceRunner runner


    def setupSpec() {
        AmqpFileServerRunner.setTestRun(false)
        runner = new AmqpFileServerRunner()
        runner.runAsync()
    }

    def cleanupSpec() {
        runner.stop()
    }
}
