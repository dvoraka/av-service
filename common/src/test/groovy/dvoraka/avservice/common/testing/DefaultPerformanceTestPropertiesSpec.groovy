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
            String destQueue = 'destinationTestQueue'
            int msgCount = 999
            boolean synchronous = true
            boolean sendOnly = true

        when:
            properties = new DefaultPerformanceTestProperties.Builder()
                    .destinationQueue(destQueue)
                    .msgCount(msgCount)
                    .synchronous(synchronous)
                    .sendOnly(sendOnly)
                    .build()

        then:
            with(properties) {
                getDestinationQueue() == destQueue
                getMsgCount() == msgCount
                isSynchronous() == synchronous
                isSendOnly() == sendOnly
            }
    }
}
