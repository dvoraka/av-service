package dvoraka.avservice.client.example

import spock.lang.Specification

import java.util.concurrent.CancellationException

/**
 * Example spec.
 */
class AvCheckExampleSpec extends Specification {

    def "run example"() {
        when:
            AvCheckExample.main(null)

        then:
            thrown(CancellationException)
    }
}
