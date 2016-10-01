package dvoraka.avservice.common.data

import dvoraka.avservice.common.Utils
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Performance test for AvMessageMapper.
 */
class AvMessageMapperPTest extends Specification {

    @Unroll
    def "Transform AvM to M: #cycles times"() {
        given:
            AvMessage avMessage = Utils.genNormalMessage()

        when:
            cycles.times {
                AvMessageMapper.transform(avMessage)
            }

        then:
            notThrown(Exception)

        where:
            cycles << [1_000, 10_000, 100_000, 1000_000, 10_000_000, 100_000_000]
    }
}
