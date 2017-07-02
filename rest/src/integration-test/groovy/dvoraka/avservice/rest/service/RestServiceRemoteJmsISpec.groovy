package dvoraka.avservice.rest.service

import dvoraka.avservice.common.runner.ServiceRunner
import dvoraka.avservice.rest.Application
import dvoraka.avservice.runner.server.jms.JmsFileServerRunner
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Shared

/**
 * REST with a remote JMS connection and file storage.
 */
@SpringBootTest(
        classes = [
                Application.class
        ],
        properties = [
                'spring.profiles.active=client,rest,rest-remote,jms,file-client,storage,db',
                'server.contextPath=/av-service',
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DirtiesContext
class RestServiceRemoteJmsISpec extends RestServiceISpec {

    @Shared
    ServiceRunner runner


    def setupSpec() {
        JmsFileServerRunner.setTestRun(false)
        runner = new JmsFileServerRunner()
        runner.runAsync()
        sleep(4_000) // wait for server
    }

    def cleanupSpec() {
        runner.stop()
    }
}
