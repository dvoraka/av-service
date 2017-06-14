package dvoraka.avservice.db.configuration

import dvoraka.avservice.db.service.MessageInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [DatabaseConfig.class])
@ActiveProfiles(['db-mem'])
class DbMemConfigISpec extends Specification {

    @Autowired
    MessageInfoService service


    def "test"() {
        expect:
            true
    }
}
