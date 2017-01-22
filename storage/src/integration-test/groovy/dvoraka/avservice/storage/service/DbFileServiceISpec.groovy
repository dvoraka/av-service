package dvoraka.avservice.storage.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.db.configuration.DatabaseConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * DB file service spec.
 */
@ContextConfiguration(classes = [DatabaseConfig.class])
@ActiveProfiles(['db'])
@Ignore('WIP')
class DbFileServiceISpec extends Specification {

    @Autowired
    FileService service


    def "save file"() {
        expect:
            service.saveFile(Utils.genFileMessage())
    }

    def "save and load file"() {
        given:
            AvMessage message = Utils.genFileMessage()

        when:
            service.saveFile(message)
            FileMessage response = service.loadFile(message.getFilename(), message.getOwner())

        then:
            message.owner == response.owner
    }
}
