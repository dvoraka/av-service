package dvoraka.avservice.checker

import dvoraka.avservice.checker.exception.LastMessageException
import dvoraka.avservice.checker.exception.ProtocolException
import dvoraka.avservice.checker.receiver.AvReceiver
import spock.lang.Specification

/**
 * Load tester spec.
 */
class LoadTesterSpec extends Specification {

    LoadTester loadTester

    def setup() {
        loadTester = new LoadTester(null)
    }

    def "constructor"() {
        when:
            loadTester = new LoadTester(null)

        then:
            loadTester.getProps()
    }

    def "receive messages with empty collection"() {
        when:
            loadTester.receiveMessages(new ArrayList<String>())

        then:
            notThrown(Exception)
    }

    def "receive messages with 1 msg"() {
        given:
            loadTester = Spy()
            loadTester.getAvReceiver() >> Mock(AvReceiver)

            List<String> ids = new ArrayList<>()
            ids.add("test1")

        when:
            loadTester.receiveMessages(ids)

        then:
            notThrown(Exception)
    }

    def "receive messages with some troubles"() {
        given:
            loadTester = Spy()

            AvReceiver avReceiver = Stub()
            avReceiver.receive(_) >> {
                throw new LastMessageException()
            } >> {
                throw new ProtocolException()
            } >> false

            loadTester.getAvReceiver() >> avReceiver

            List<String> ids = new ArrayList<>()
            ids.add("test1")

        when:
            loadTester.receiveMessages(ids)

        then:
            notThrown(Exception)
    }
}
