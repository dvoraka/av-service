package dvoraka.avservice.client

import dvoraka.avservice.client.service.AvServiceClient
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.util.Utils
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
    @Shared
    int threads = 4

    ExecutorService executorService


    def setup() {
        executorService = Executors.newFixedThreadPool(threads)
    }

    def cleanup() {
        executorService.shutdownNow()
    }

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
            Runnable task = { avServiceClient.checkMessage(message) }

        expect:
            loops.times {
                executorService.submit(task)
            }
    }

    def "send random messages to queue concurrently"() {
        given:
            Runnable task = { avServiceClient.checkMessage(Utils.genMessage()) }

        expect:
            loops.times {
                executorService.submit(task)
            }
    }
}
