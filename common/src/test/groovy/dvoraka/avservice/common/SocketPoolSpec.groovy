package dvoraka.avservice.common

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Subject

/**
 * Socket pool spec.
 */
@Ignore('WIP')
class SocketPoolSpec extends Specification {

    @Subject
    SocketPool socketPool


    def setup() {
        socketPool = new SocketPool(5, 'localhost', 3310)
    }

    def "test"() {
    }
}
