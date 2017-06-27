package dvoraka.avservice.client.example

import spock.lang.Specification

/**
 * Example spec.
 */
class AvCheckExampleSpec extends Specification {

    def "run example"() {
        when:
            AvCheckExample.main(null)

        then:
            notThrown(Exception)
    }
}
