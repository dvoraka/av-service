package dvoraka.avservice.common.testing

import spock.lang.Specification
import spock.lang.Subject

/**
 * DefaultPerformanceTestProperties spec.
 */
class DefaultPerformanceTestPropertiesSpec extends Specification {

    @Subject
    DefaultPerformanceTestProperties properties


    def "builder test"() {
        given:
            int msgCount = 999
            boolean sendOnly = true
            long maxRate = 12

        when:
            properties = new DefaultPerformanceTestProperties.Builder()
                    .msgCount(msgCount)
                    .sendOnly(sendOnly)
                    .maxRate(12)
                    .build()

        then:
            with(properties) {
                getMsgCount() == msgCount
                isSendOnly() == sendOnly
                getMaxRate() == maxRate
            }
    }

    def "to string"() {
        given:
            properties = new DefaultPerformanceTestProperties.Builder()
                    .build()

        expect:
            properties.toString().endsWith('}')
    }
}
