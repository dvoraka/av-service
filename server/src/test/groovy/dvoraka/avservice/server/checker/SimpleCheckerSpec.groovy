package dvoraka.avservice.server.checker

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.DefaultAvMessage
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

            String corrId = 'X-CID-TEST'
            AvMessage message = new DefaultAvMessage.Builder(null)
                    .correlationId(corrId)
                    .build()

        when: "fill the queue"
            new Thread(
                    {
                        4.times {
                            checker.onAvMessage(Utils.genNormalMessage())
                        }
                        // 5th message
                        checker.onAvMessage(message)
                    }
            ).start()

        then: "receiving lost some messages"
            checker.receiveMessage(corrId) == message
    }
}
