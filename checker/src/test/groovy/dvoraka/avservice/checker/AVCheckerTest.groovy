package dvoraka.avservice.checker

import dvoraka.avservice.checker.receiver.Receiver
import dvoraka.avservice.checker.sender.Sender
import spock.lang.*;


class AVCheckerTest extends Specification {

    def "test check calls"() {

        setup:
            Sender sender = Mock()
            Receiver receiver = Mock()
            AVChecker checker = new AVChecker(sender, receiver)

        when:
            checker.check()

        then:
            1 * sender.sendFile(_, _)
            1 * receiver.receive(_)
    }
}
