package dvoraka.avservice.checker.utils

import dvoraka.avservice.checker.receiver.AvReceiver
import dvoraka.avservice.checker.sender.AvSender
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification

/**
 * AVUtils test.
 */
class AVUtilsSpec extends Specification {

    AVUtils avUtils


    def setup() {
        avUtils = new AVUtils()
    }

    def "try protocol version"() {
        given:
        String protocolVersion = "10"

        AvSender avSender = Mock()
        AvReceiver avReceiver = Mock()
        setSenderAndReceiver(avSender, avReceiver)

        when:
        boolean works = avUtils.tryProtocolVersion(protocolVersion)

        then:
        works
    }

    def "find protocol version"() {
        given:
        String[] protocols = ["10", "20"]

        AvSender avSender = Mock()
        AvReceiver avReceiver = Mock()
        setSenderAndReceiver(avSender, avReceiver)

        when:
        String foundProtocol = avUtils.findProtocolVersion(protocols)

        then:
        foundProtocol.equals(protocols[0])
    }

    def "negotiate procol"() {
        given:
        String[] protocols = ["10", "20"]

        AvSender avSender = Mock()
        AvReceiver avReceiver = Mock()
        setSenderAndReceiver(avSender, avReceiver)

        when:
        String foundProtocol = avUtils.negotiateProtocol(protocols)

        then:
        notThrown(UnknownHostException)
        foundProtocol.equals(protocols[0])
    }

    void setSenderAndReceiver(AvSender sender, AvReceiver receiver) {
        ReflectionTestUtils.setField(avUtils, null, sender, AvSender.class)
        ReflectionTestUtils.setField(avUtils, null, receiver, AvReceiver.class)
    }
}
