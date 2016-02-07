package dvoraka.avservice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Specification class for AVProgram.
 */
@ContextConfiguration(classes = [AppConfig])
class AVProgramSpec extends Specification {

    @Autowired
    AVProgram avProgram;

    def setup() {
    }

    def "default test"() {
        expect:
        true
    }

    def "AV program loading"() {
        expect:
        avProgram != null
    }

    def "scan normal bytes"() {
        setup:
        byte[] bytes = "aaaaa".getBytes()

        when:
        boolean shouldBeFalse = avProgram.scanStream(bytes)

        then:
        !shouldBeFalse
    }
}
