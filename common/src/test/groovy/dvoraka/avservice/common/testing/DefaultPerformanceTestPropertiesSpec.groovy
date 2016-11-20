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

    def "configuration with a default configuration file"() {
        when:
            properties = new DefaultPerformanceTestProperties()

        then:
            properties.getHost() == 'testhost'
    }

    def "builder test"() {
        given:
            String host = 'testHost'
            String vHost = 'virtualTestHost'
            String appId = 'testAppID'
            String destQueue = 'destinationTestQueue'
            int msgCount = 999
            boolean synchronous = true
            boolean sendOnly = true

        when:
            properties = new DefaultPerformanceTestProperties.Builder()
                    .host(host)
                    .virtualHost(vHost)
                    .appId(appId)
                    .destinationQueue(destQueue)
                    .msgCount(msgCount)
                    .synchronous(synchronous)
                    .sendOnly(sendOnly)
                    .build()

        then:
            with(properties) {
                getHost() == host
                getVirtualHost() == vHost
                getAppId() == appId
                getDestinationQueue() == destQueue
                getMsgCount() == msgCount
                isSynchronous() == synchronous
                isSendOnly() == sendOnly
            }
    }
}
