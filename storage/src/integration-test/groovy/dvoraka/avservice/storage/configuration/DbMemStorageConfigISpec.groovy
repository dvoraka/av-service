package dvoraka.avservice.storage.configuration

import dvoraka.avservice.storage.service.FileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [StorageConfig.class])
@ActiveProfiles(['storage', 'db-mem'])
@DirtiesContext
class DbMemStorageConfigISpec extends Specification {

    @Autowired
    FileService service


    def "test"() {
        expect:
            true
    }
}
