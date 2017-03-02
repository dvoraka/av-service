package dvoraka.avservice.avprogram.configuration

import dvoraka.avservice.avprogram.AvProgram
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Config spec.
 */
@ContextConfiguration(classes = [AvProgramConfig.class])
@ActiveProfiles('core')
class AvProgramConfigISpec extends Specification {

    @Autowired
    AvProgram avProgram


    def "test"() {
        expect:
            true
    }
}
