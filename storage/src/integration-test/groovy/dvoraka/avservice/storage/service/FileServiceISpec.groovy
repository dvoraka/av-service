package dvoraka.avservice.storage.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.DefaultAvMessage
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.common.helper.FileServiceHelper
import dvoraka.avservice.storage.ExistingFileException
import dvoraka.avservice.storage.FileNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

/**
 * File service spec.
 */
//TODO: replace builders
@Ignore
class FileServiceISpec extends Specification implements FileServiceHelper {

    @Autowired
    FileService service

    String testingFile = 'testing file'
    String testingOwner = 'testing user'


    def "save file"() {
        expect:
            service.saveFile(Utils.genFileMessage())
    }

    @Unroll
    def "save file with #size bytes"() {
        given:
            FileMessage message = fileSaveMessage(testingFile, testingOwner, new byte[size])

        when:
            service.saveFile(message)
            service.deleteFile(message)

        then:
            notThrown(Exception)

        where:
            size << [10, 1 * 1000, 1000 * 1000]
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
            FileMessage saveMessage = Utils.genFileMessage(testingOwner)
            FileMessage loadMessage = fileLoadMessage(saveMessage)

        when:
            service.saveFile(saveMessage)

        then:
            service.exists(saveMessage.getFilename(), saveMessage.getOwner())

        when:
            FileMessage response = service.loadFile(loadMessage)

        then:
            saveMessage.getOwner() == response.getOwner()
            saveMessage.getFilename() == response.getFilename()
            Arrays.equals(saveMessage.getData(), response.getData())

            loadMessage.getId() == response.getCorrelationId()
    }

    def "save and delete file"() {
        given:
            FileMessage saveMessage = Utils.genFileMessage(testingOwner)
            FileMessage loadMessage = fileLoadMessage(saveMessage)
            FileMessage deleteMessage = fileDeleteMessage(
                    saveMessage.getFilename(), saveMessage.getOwner())

        when:
            service.saveFile(saveMessage)

        then:
            service.exists(saveMessage)

        when:
            service.deleteFile(deleteMessage)

        then:
            !service.exists(saveMessage)

        when:
            FileMessage notFound = service.loadFile(loadMessage)

        then:
            notFound.getType() == MessageType.FILE_NOT_FOUND
    }

    def "save and update file"() {
        given:
            FileMessage saveMessage = Utils.genFileMessage(testingOwner)
            FileMessage loadMessage = fileLoadMessage(saveMessage)
            byte[] newData = new byte[3]
            FileMessage updateMessage = fileUpdateMessage(saveMessage, newData)

        when:
            service.saveFile(saveMessage)

        then:
            service.exists(saveMessage)

        when:
            FileMessage response = service.loadFile(loadMessage)

        then:
            !Arrays.equals(response.getData(), newData)

        when:
            service.updateFile(updateMessage)
            FileMessage updatedFile = service.loadFile(loadMessage)

        then:
            Arrays.equals(updatedFile.getData(), newData)
    }

    def "load non-existent file"() {
        given:
            FileMessage saveMessage = Utils.genFileMessage(testingOwner)
            FileMessage loadMessage = fileLoadMessage(saveMessage)

        expect:
            service.loadFile(loadMessage).getType() == MessageType.FILE_NOT_FOUND
    }

    def "update non-existent file"() {
        given:
            FileMessage message = Utils.genFileMessage(testingOwner)
            byte[] newData = new byte[3]
            FileMessage updateRequest = fileUpdateMessage(message, newData)

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
