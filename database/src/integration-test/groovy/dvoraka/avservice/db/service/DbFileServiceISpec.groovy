package dvoraka.avservice.db.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.db.configuration.DatabaseConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * DB file service spec.
 */
@ContextConfiguration(classes = [DatabaseConfig.class])
@ActiveProfiles(['db'])
class DbFileServiceISpec extends Specification {

    @Autowired
    FileService service


    def "save file"() {
        expect:
            service.saveFile((FileMessage) Utils.genNormalMessage())
    }
}
