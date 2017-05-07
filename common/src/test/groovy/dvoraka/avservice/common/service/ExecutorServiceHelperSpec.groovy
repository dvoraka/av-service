package dvoraka.avservice.common.service

import org.apache.logging.log4j.Logger
import spock.lang.Specification
import spock.lang.Subject

import java.util.concurrent.ExecutorService

/**
 * ExecutorServiceHelperSpec
 */
class ExecutorServiceHelperSpec extends Specification {

    @Subject
    ExecutorServiceHelper helper

    ExecutorService service
    Logger logger

    long seconds = 10


    def setup() {
        helper = Spy()

        service = Mock()
        logger = Mock()
    }

    def "normal shutdown"() {
        when:
            helper.shutdownAndAwaitTermination(service, seconds, logger)

        then:
            1 * service.shutdown()
            1 * service.awaitTermination(_, _) >> true

            2 * logger._
    }

    def "shutdown with troubles"() {
        when:
            helper.shutdownAndAwaitTermination(service, seconds, logger)

        then:
            1 * service.shutdown()
            2 * service.awaitTermination(_, _) >> false >> true
            1 * service.shutdownNow()

            2 * logger._
    }

    def "shutdown with big troubles"() {
        when:
            helper.shutdownAndAwaitTermination(service, seconds, logger)

        then:
            1 * service.shutdown()
            2 * service.awaitTermination(_, _) >> false
            1 * service.shutdownNow()

            1 * logger.warn(_)
            1 * logger._
    }

    def "shutdown with waiting troubles"() {
        when:
            helper.shutdownAndAwaitTermination(service, seconds, logger)

        then:
            1 * service.shutdown()
            1 * service.awaitTermination(_, _) >> { throw new InterruptedException() }

            1 * service.shutdownNow()

            1 * logger.warn(_, _)
            1 * logger._
    }
}
