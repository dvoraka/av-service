package dvoraka.avservice.avprogram

import dvoraka.avservice.avprogram.configuration.AvProgramConfig
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.service.CachingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

/**
 * AvProgram spec.
 */
@ContextConfiguration(classes = [AvProgramConfig.class])
@ActiveProfiles(['core'])
class AvProgramISpec extends Specification {

    @Autowired
    AvProgram avProgram

    @Autowired
    CachingService cachingService

    @Shared
    String eicarString = Utils.EICAR


    def setup() {
    }

    def "AV program loading"() {
        expect:
            avProgram
    }

    def "Is program running?"() {
        expect:
            avProgram.isRunning()
    }

    def "scan normal bytes"() {
        setup:
            byte[] bytes = "No virus here".getBytes("UTF-8")

        when:
            boolean shouldBeFalse = avProgram.scanBytes(bytes)

        then:
            !shouldBeFalse
    }

    def "scan normal bytes with caching enabled"() {
        setup:
            // skip test when caching service is not available
            if (cachingService == null) {
                return
            }

            byte[] bytes = "No virus here".getBytes("UTF-8")
            avProgram.setCaching(true)

        expect:
            avProgram.isCaching()

        when:
            boolean shouldBeFalse = avProgram.scanBytes(bytes)
            boolean fromCache = avProgram.scanBytes(bytes)

        then:
            !shouldBeFalse
            !fromCache

        cleanup:
            avProgram.setCaching(false)
    }

    def "scan eicar bytes"() {
        setup:
            byte[] bytes = eicarString.getBytes("UTF-8")

        when:
            boolean shouldBeTrue = avProgram.scanBytes(bytes)

        then:
            shouldBeTrue
    }
}
