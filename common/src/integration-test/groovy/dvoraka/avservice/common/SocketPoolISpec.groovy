package dvoraka.avservice.common

import spock.lang.Specification
import spock.lang.Subject

/**
 * Socket pool spec.
 */
class SocketPoolISpec extends Specification {

    @Subject
    SocketPool socketPool


    def setup() {
        socketPool = new SocketPool(5, 'localhost', 3310)
    }

    def "close pool"() {
        expect:
            socketPool.close()
    }
}
