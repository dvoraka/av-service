package dvoraka.avservice.checker.utils

import spock.lang.Specification

/**
 * Config parser test.
 */
class LoadTestConfigParserISpec extends Specification {

    LoadTestConfigParser configParser


    def setup() {
        configParser = new LoadTestConfigParser()
    }

    def "parsing test"() {
        given:
            String filename = getClass().getResource("/loadTestTestingFile.xml").toString()
            Map<String, String> props

        when:
            configParser.parseFileSax(filename)
            props = configParser.getProperties()

        then:
            props.get("appId").equals("TestId")
            props.get("destinationQueue").equals("TestDestination")
            props.get("host").equals("TestHost")
            props.get("messageCount").equals("10")
            props.get("sendOnly").equals("false")
            props.get("synchronous").equals("true")
            props.get("virtualHost").equals("AntivirusTest")
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
            props.get("host").equals("TestHost")
            props.get("virtualHost").equals("AntivirusTest")
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
