package dvoraka.avservice.client

import dvoraka.avservice.client.service.AvServiceClient
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Client performance spec.
 */
@Ignore('base class')
class ClientPSpec extends Specification {

    @Autowired
    AvServiceClient avServiceClient

    @Shared
    int loops = 10_000_000


    def "test config"() {
        expect:
            true
    }

    def "send same messages to queue"() {
        given:
            AvMessage message = Utils.genMessage()

        expect:
            loops.times {
                avServiceClient.checkMessage(message)
            }
    }

    def "send random messages to queue"() {
        expect:
            loops.times {
                avServiceClient.checkMessage(Utils.genMessage())
            }
    }

    def "send same messages to queue concurrently"() {
        given:
            AvMessage message = Utils.genMessage()
            int threads = 4

            ExecutorService executorService = Executors.newFixedThreadPool(threads)
            Runnable task = { avServiceClient.checkMessage(message) }

        expect:
            loops.times {
                executorService.submit(task)
            }
    }

    def "send random messages to queue concurrently"() {
        given:
            int threads = 4

            ExecutorService executorService = Executors.newFixedThreadPool(threads)
            Runnable task = { avServiceClient.checkMessage(Utils.genMessage()) }

        expect:
            loops.times {
                executorService.submit(task)
            }
    }
}
