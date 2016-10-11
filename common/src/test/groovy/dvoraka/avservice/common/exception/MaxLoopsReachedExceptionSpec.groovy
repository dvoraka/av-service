package dvoraka.avservice.common.exception

import spock.lang.Specification

/**
 * Exception spec.
 */
class MaxLoopsReachedExceptionSpec extends Specification {

    def "constructor"() {
        when:
            throw new MaxLoopsReachedException()

        then:
            thrown(MaxLoopsReachedException)
    }
}
