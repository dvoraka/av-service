package dvoraka.avservice.storage.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.DefaultAvMessage
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.storage.ExistingFileException
import dvoraka.avservice.storage.FileNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Specification

/**
 * File service spec.
 */
@Ignore
class FileServiceISpec extends Specification {

    @Autowired
    FileService service

    String testingOwner = 'testing user'


    def "save file"() {
        expect:
            service.saveFile(Utils.genFileMessage())
    }

    def "save same file twice"() {
        given:
            FileMessage message = Utils.genFileMessage()

        when:
            service.saveFile(message)
            service.saveFile(message)

        then:
            thrown(ExistingFileException)
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

        then:
            service.exists(message.getFilename(), message.getOwner())

        when:
            FileMessage response = service.loadFile(request)

        then:
            message.getOwner() == response.getOwner()
            message.getFilename() == response.getFilename()

            request.getId() == response.getCorrelationId()
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

        then:
            service.exists(message)

        when:
            service.deleteFile(deleteRequest)

        then:
            !service.exists(message)

        when:
            FileMessage notFound = service.loadFile(loadRequest)

        then:
            notFound.getType() == MessageType.FILE_NOT_FOUND
    }

    def "save and update file"() {
        given:
            AvMessage message = Utils.genFileMessage(testingOwner)

            AvMessage loadRequest = new DefaultAvMessage.Builder(Utils.genUuidString())
                    .filename(message.getFilename())
                    .owner(message.getOwner())
                    .type(MessageType.FILE_LOAD)
                    .build()

            byte[] newData = new byte[3]
            AvMessage updateRequest = new DefaultAvMessage.Builder(Utils.genUuidString())
                    .filename(message.getFilename())
                    .owner(message.getOwner())
                    .data(newData)
                    .type(MessageType.FILE_UPDATE)
                    .build()

        when:
            service.saveFile(message)

        then:
            service.exists(message)

        when:
            FileMessage response = service.loadFile(loadRequest)

        then:
            !Arrays.equals(response.getData(), newData)

        when:
            service.updateFile(updateRequest)
            FileMessage updatedFile = service.loadFile(loadRequest)

        then:
            Arrays.equals(updatedFile.getData(), newData)
    }

    def "load non-existent file"() {
        given:
            AvMessage message = Utils.genFileMessage(testingOwner)
            AvMessage loadRequest = new DefaultAvMessage.Builder(Utils.genUuidString())
                    .filename(message.getFilename())
                    .owner(message.getOwner())
                    .type(MessageType.FILE_LOAD)
                    .build()

        expect:
            service.loadFile(loadRequest).getType() == MessageType.FILE_NOT_FOUND
    }

    def "update non-existent file"() {
        given:
            AvMessage message = Utils.genFileMessage(testingOwner)

            byte[] newData = new byte[3]
            AvMessage updateRequest = new DefaultAvMessage.Builder(Utils.genUuidString())
                    .filename(message.getFilename())
                    .owner(message.getOwner())
                    .data(newData)
                    .type(MessageType.FILE_UPDATE)
                    .build()

        when:
            service.updateFile(updateRequest)

        then:
            thrown(FileNotFoundException)
    }

    def "delete non-existent file"() {
        given:
            AvMessage message = Utils.genFileMessage(testingOwner)
            AvMessage deleteRequest = new DefaultAvMessage.Builder(Utils.genUuidString())
                    .filename(message.getFilename())
                    .owner(message.getOwner())
                    .type(MessageType.FILE_DELETE)
                    .build()

        when:
            service.deleteFile(deleteRequest)

        then:
            !service.exists(message)
    }
}
