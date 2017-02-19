package dvoraka.avservice.common.testing

import spock.lang.Specification
import spock.lang.Subject

/**
 * DefaultPerformanceTestProperties spec.
 */
class DefaultPerformanceTestPropertiesSpec extends Specification {

    @Subject
    DefaultPerformanceTestProperties properties


    def "configuration without a configuration file"() {
        when:
            properties = new DefaultPerformanceTestProperties("NonEfile")

        then:
            notThrown(Exception)
    }

    def "builder test"() {
        given:
            int msgCount = 999
            boolean sendOnly = true

        when:
            properties = new DefaultPerformanceTestProperties.Builder()
                    .msgCount(msgCount)
                    .sendOnly(sendOnly)
                    .build()

        then:
            with(properties) {
                getMsgCount() == msgCount
                isSendOnly() == sendOnly
            }
    }
}
