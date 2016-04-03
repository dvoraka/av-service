package dvoraka.avservice

import dvoraka.avservice.common.Utils
import dvoraka.avservice.configuration.AppConfig
import dvoraka.avservice.service.AVService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

/**
 * Specification class for AVService.
 */
@ContextConfiguration(classes = [AppConfig])
class AVServiceISpec extends Specification {

    @Shared
    String eicarString = Utils.EICAR

    @Autowired
    AVService avService;


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

    def "scan infected bytes"() {
        setup:
        byte[] bytes = eicarString.getBytes()

        when:
        boolean shouldBeTrue = avService.scanStream(bytes)

        then:
        shouldBeTrue
    }

    def "scan normal file"() {
        setup:
        File file = File.createTempFile("test-avtempfile", ".tmp")
        file.deleteOnExit()

        when:
        boolean shouldBeFalse = avService.scanFile(file)

        then:
        !shouldBeFalse
    }
}
