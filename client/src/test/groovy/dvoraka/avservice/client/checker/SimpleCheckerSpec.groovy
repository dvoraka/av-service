package dvoraka.avservice.client.checker

import dvoraka.avservice.client.transport.AvNetworkComponent
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.DefaultAvMessage
import dvoraka.avservice.common.exception.MessageNotFoundException
import dvoraka.avservice.common.util.Utils
import spock.lang.Specification
import spock.lang.Subject

/**
 * SimpleChecker spec.
 */
class SimpleCheckerSpec extends Specification {

    @Subject
    SimpleChecker checker


    def "check with troubles"() {
        given:
            checker = Spy(constructorArgs: [Mock(AvNetworkComponent)])
            checker.receiveMessage(_) >>
                    Utils.genMessage().createCheckResponse(Utils.OK_VIRUS_INFO) >>
                    { throw new MessageNotFoundException() }

        expect:
            !checker.check()
    }
}
