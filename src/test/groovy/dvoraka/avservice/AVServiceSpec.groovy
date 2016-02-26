package dvoraka.avservice

import dvoraka.avservice.configuration.AppConfig
import dvoraka.avservice.service.AVService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Specification class for AVService.
 */
@ContextConfiguration(classes = [AppConfig])
class AVServiceSpec extends Specification {

    @Autowired
    AVService avService;

    def "default test"() {
        expect:
        true
    }

    def "AV service loading"() {
        expect:
        avService != null
    }

    def "scan normal bytes"() {
        setup:
        byte[] bytes = "aaaaa".getBytes()

        when:
        boolean shouldBeFalse = avService.scanStream(bytes)

        then:
        !shouldBeFalse
    }

    def "scan normal file"() {
        setup:
        File file = File.createTempFile("avtempfile", ".tmp")

        when:
        boolean shouldBeFalse = avService.scanFile(file)

        then:
        !shouldBeFalse
    }
}
