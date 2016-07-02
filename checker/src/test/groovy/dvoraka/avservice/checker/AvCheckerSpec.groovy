package dvoraka.avservice.checker

import dvoraka.avservice.checker.exception.LastMessageException
import dvoraka.avservice.checker.receiver.AvReceiver
import dvoraka.avservice.checker.sender.AvSender
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification;

class AvCheckerSpec extends Specification {

    AvChecker checker


    def "test check calls"() {
        given:
        AvSender sender = Mock()
        AvReceiver receiver = Mock()

        checker = new AvChecker(false, "antivirus")
        setAvSenderReceiver(sender, receiver)

        when:
        checker.check()

        then:
        notThrown(Exception)
        1 * sender.sendFile(_, _)
        1 * receiver.receive(_)
    }

    def "check with ConnectException"() {
        given:
        AvSender sender = Stub()
        sender.sendFile(_, _) >> {
            throw new ConnectException()
        }
        AvReceiver receiver = Mock()

        checker = new AvChecker(false, "antivirus")
        setAvSenderReceiver(sender, receiver)

        when:
        checker.check()

        then:
        notThrown(Exception)
    }

    def "check with IOException"() {
        given:
        AvSender sender = Stub()
        sender.sendFile(_, _) >> {
            throw new IOException()
        }
        AvReceiver receiver = Mock()

        checker = new AvChecker(false, "antivirus")
        setAvSenderReceiver(sender, receiver)

        when:
        checker.check()

        then:
        notThrown(Exception)
    }

    def "check with LastMessageException"() {
        given:
        AvSender sender = Mock()
        AvReceiver receiver = Stub()
        receiver.receive(_) >> {
            throw new LastMessageException()
        }

        checker = new AvChecker(false, "antivirus")
        setAvSenderReceiver(sender, receiver)

        when:
        checker.check()

        then:
        notThrown(Exception)
    }

    def "check with a test problem"() {
        given:
        AvSender sender = Mock()
        AvReceiver receiver = Stub()
        receiver.receive(_) >> true

        checker = new AvChecker(false, "antivirus")
        setAvSenderReceiver(sender, receiver)

        when:
        checker.check()

        then:
        notThrown(Exception)
    }

    void setAvSenderReceiver(AvSender sender, AvReceiver receiver) {
        ReflectionTestUtils.setField(checker, null, sender, AvSender.class)
        ReflectionTestUtils.setField(checker, null, receiver, AvReceiver.class)
    }
}
