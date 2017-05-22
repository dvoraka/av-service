package dvoraka.avservice.rest.service

import dvoraka.avservice.common.runner.ServiceRunner
import dvoraka.avservice.rest.Application
import dvoraka.avservice.server.runner.amqp.AmqpFileServerRunner
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Shared

/**
 * REST with a remote AMQP connection and file storage.
 */
@SpringBootTest(
        classes = [
                Application.class
        ],
        properties = [
                'spring.profiles.active=client,rest,rest-remote,amqp,file-client,storage,db',
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
        sleep(4_000) // wait for server
    }

    def cleanupSpec() {
        runner.stop()
    }
}
