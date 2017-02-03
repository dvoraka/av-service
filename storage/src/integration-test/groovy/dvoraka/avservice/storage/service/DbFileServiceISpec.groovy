package dvoraka.avservice.storage.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.DefaultAvMessage
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.storage.configuration.StorageConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * DB file service spec.
 */
@ContextConfiguration(classes = [StorageConfig.class])
@ActiveProfiles(['storage', 'db'])
class DbFileServiceISpec extends Specification {

    @Autowired
    FileService service

    String testingOwner = 'testing user'


    def "save file"() {
        expect:
            service.saveFile(Utils.genFileMessage())
    }

    def "save and load file"() {
        given:
            AvMessage message = Utils.genFileMessage(testingOwner)
            AvMessage request = new DefaultAvMessage.Builder(Utils.genUuidString())
                    .filename(message.getFilename())
                    .owner(message.getOwner())
                    .type(MessageType.FILE_LOAD)
                    .build()

        when:
            service.saveFile(message)
            FileMessage response = service.loadFile(request)

        then:
            message.owner == response.owner
            message.filename == response.filename
    }

    def "save and delete file"() {
        given:
            AvMessage message = Utils.genFileMessage(testingOwner)

            AvMessage loadRequest = new DefaultAvMessage.Builder(Utils.genUuidString())
                    .filename(message.getFilename())
                    .owner(message.getOwner())
                    .type(MessageType.FILE_LOAD)
                    .build()

            AvMessage deleteRequest = new DefaultAvMessage.Builder(Utils.genUuidString())
                    .filename(message.getFilename())
                    .owner(message.getOwner())
                    .type(MessageType.FILE_DELETE)
                    .build()

        when:
            service.saveFile(message)
            FileMessage response = service.loadFile(loadRequest)

        then:
            message.owner == response.owner
            message.filename == response.filename

        when:
            service.deleteFile(deleteRequest)

        then:
            service.loadFile(loadRequest).getType() == MessageType.FILE_NOT_FOUND
    }
}
