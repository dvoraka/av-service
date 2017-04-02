package dvoraka.avservice.rest.service

import dvoraka.avservice.rest.Application
import org.springframework.boot.test.context.SpringBootTest

/**
 * REST with local connections.
 */
@SpringBootTest(
        classes = [
                Application.class
        ],
        properties = [
                'spring.profiles.active=core,rest,rest-local,storage,db',
                'server.contextPath=/av-service',
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class RestServiceLocalISpec extends RestServiceISpec {
}
