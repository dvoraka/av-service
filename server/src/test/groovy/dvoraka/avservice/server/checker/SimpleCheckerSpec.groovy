package dvoraka.avservice.server.checker

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.DefaultAvMessage
import dvoraka.avservice.common.exception.MessageNotFoundException
import dvoraka.avservice.server.ServerComponent
import spock.lang.Specification
import spock.lang.Subject

/**
 * SimpleChecker spec.
 */
class SimpleCheckerSpec extends Specification {

    @Subject
    SimpleChecker checker


    def "full queue overflow"() {
        given:
            ServerComponent serverComponent = Mock()
            checker = new SimpleChecker(serverComponent, 1)

            List<AvMessage> messages = []
            2.times {
                messages << Utils.genMessage()
            }

            String corrId = 'X-CID-TEST'
            AvMessage message = new DefaultAvMessage.Builder(Utils.genUuidString())
                    .correlationId(corrId)
                    .build()

        when: "fill the queue"
            new Thread(
                    {
                        messages.each {
                            checker.onAvMessage(it)
                        }
                        // 3rd message
                        checker.onAvMessage(message)
                    }
            ).start()

        then: "receiving last message lost some older messages"
            checker.receiveMessage(corrId) == message

        when: "we want all sent messages"
            messages.each {
                checker.receiveMessage(it.getCorrelationId())
            }

        then: "it is not possible to find them"
            thrown(MessageNotFoundException)
    }
}
