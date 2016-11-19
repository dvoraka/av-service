package dvoraka.avservice.common

import dvoraka.avservice.common.testing.PerformanceTestConfigParser
import spock.lang.Specification
import spock.lang.Subject

/**
 * Performance test config parser spec.
 */
class PerformanceTestConfigParserISpec extends Specification {

    @Subject
    PerformanceTestConfigParser configParser


    def setup() {
        configParser = new PerformanceTestConfigParser()
    }

    def "parsing test"() {
        given:
            String filename = getClass().getResource("/loadTestTestingFile.xml").toString()
            Map<String, String> props

        when:
            configParser.parseFileSax(filename)
            props = configParser.getProperties()

        then:
            with(props) {
                get("appId") == "TestId"
                get("destinationQueue") == "TestDestination"
                get("host") == "TestHost"
                get("messageCount") == "10"
                get("sendOnly") == "false"
                get("synchronous") == "true"
                get("virtualHost") == "AntivirusTest"
            }
    }

    def "parsing test with a parse error"() {
        given:
            String filename = getClass().getResource("/loadTestTestingFileWithError.xml").toString()
            Map<String, String> props

        when:
            configParser.parseFileSax(filename)
            props = configParser.getProperties()

        then:
            notThrown(Exception)
            props.size() == 2
            props.get("host") == "TestHost"
            props.get("virtualHost") == "AntivirusTest"
    }

    def "parsing test without a file"() {
        given:
            String filename = getClass().getResource("/someRandomFilename1112.xml").toString()
            Map<String, String> props

        when:
            configParser.parseFileSax(filename)
            props = configParser.getProperties()

        then:
            notThrown(Exception)
            props.size() == 0
    }
}
