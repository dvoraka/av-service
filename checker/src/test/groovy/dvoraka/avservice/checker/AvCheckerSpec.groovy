package dvoraka.avservice.checker

import dvoraka.avservice.checker.receiver.AvReceiver
import dvoraka.avservice.checker.sender.AvSender
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification;

class AvCheckerSpec extends Specification {

    def "test check calls"() {
        given:
        AvSender sender = Mock()
        AvReceiver receiver = Mock()
        AvChecker checker = new AvChecker(false, "antivirus")
        ReflectionTestUtils.setField(checker, null, sender, AvSender.class)
        ReflectionTestUtils.setField(checker, null, receiver, AvReceiver.class)

        when:
        checker.check()

        then:
        1 * sender.sendFile(_, _)
        1 * receiver.receive(_)
    }
}
