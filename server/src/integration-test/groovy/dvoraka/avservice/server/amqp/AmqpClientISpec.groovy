package dvoraka.avservice.server.amqp

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.server.configuration.AmqpClientConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.StopWatch
import spock.lang.Ignore
import spock.lang.Specification

/**
 * AMQP testing.
 */
@Ignore("manual testing")
@ContextConfiguration(classes = [AmqpClientConfig.class])
@ActiveProfiles(["amqp-client"])
class AmqpClientISpec extends Specification {

    @Autowired
    Environment env
    @Autowired
    AmqpClient amqpClient


    def "send message to check exchange"() {
        when:
            amqpClient.sendMessage(
                    Utils.genNormalMessage(),
                    amqpClient.getDefaultExchange())

        then:
            notThrown(Exception)
    }

    def "get message from result queue"() {
        when:
            AvMessage avMessage = amqpClient.receiveMessage()
            println(avMessage)

        then:
            notThrown(Exception)
    }

    def "send request and receive response"() {
        when:
            AvMessage request = Utils.genNormalMessage()
            amqpClient.sendMessage(
                    request,
                    amqpClient.getDefaultExchange()
            )
            println(request)

            AvMessage response = getResponse(request.getId())
            println(response)

        then:
            notThrown(Exception)
    }

    AvMessage getResponse(String uuid) {
        StopWatch stopWatch = new StopWatch()
        stopWatch.start()

        AvMessage response
        while (true) {
            response = amqpClient.receiveMessage()
            if (response?.getCorrelationId() == uuid) {
                break
            }

            stopWatch.stop()
            if (stopWatch.getTotalTimeMillis() > 5000) {
                break
            }
            stopWatch.start()
        }

        return response
    }
}
