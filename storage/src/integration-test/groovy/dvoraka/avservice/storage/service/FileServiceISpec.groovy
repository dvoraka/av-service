package dvoraka.avservice.storage.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.FileMessage
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.common.helper.FileServiceHelper
import dvoraka.avservice.storage.exception.ExistingFileException
import dvoraka.avservice.storage.exception.FileNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * File service spec.
 */
@Ignore('base class')
class FileServiceISpec extends Specification implements FileServiceHelper {

    @Autowired
    FileService service

    @Shared
    String testingFile = 'testing file'
    @Shared
    String testingOwner = 'testing user'


    def cleanup() {
        service.deleteFile(fileDeleteMessage(testingFile, testingOwner))
    }

    def "save file"() {
        expect:
            service.saveFile(Utils.genFileMessage())
    }

    @Unroll
    def "save file with #size bytes"() {
        given:
            FileMessage saveMessage = fileSaveMessage(testingFile, testingOwner, new byte[size])
            FileMessage deleteMessage = fileDeleteMessage(saveMessage)

        when:
            service.saveFile(saveMessage)
            service.deleteFile(deleteMessage)

        then:
            notThrown(Exception)

        where:
            size << [10, 1 * 1000, 1000 * 1000]
    }

    def "save file with bad type"() {
        when:
            service.saveFile(Utils.genLoadMessage())

        then:
            thrown(IllegalArgumentException)
    }

    def "save same file twice"() {
        given:
            FileMessage saveMessage = Utils.genFileMessage()

        when:
            service.saveFile(saveMessage)
            service.saveFile(saveMessage)

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
            FileMessage deleteMessage = fileDeleteMessage(saveMessage)

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
            FileMessage updatedMessage = service.loadFile(loadMessage)

        then:
            Arrays.equals(updatedMessage.getData(), newData)
    }

    def "load non-existent file"() {
        given:
            FileMessage saveMessage = Utils.genFileMessage(testingOwner)
            FileMessage loadMessage = fileLoadMessage(saveMessage)

        expect:
            service.loadFile(loadMessage).getType() == MessageType.FILE_NOT_FOUND
    }

    def "load file with bad type"() {
        when:
            service.loadFile(Utils.genSaveMessage())

        then:
            thrown(IllegalArgumentException)
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

    def "update file with bad type"() {
        when:
            service.updateFile(Utils.genSaveMessage())

        then:
            thrown(IllegalArgumentException)
    }

    def "delete non-existent file"() {
        given:
            FileMessage saveMessage = Utils.genFileMessage(testingOwner)
            FileMessage deleteRequest = fileDeleteMessage(saveMessage)

        when:
            service.deleteFile(deleteRequest)

        then:
            !service.exists(saveMessage)
    }

    def "delete file with bad type"() {
        when:
            service.updateFile(Utils.genSaveMessage())

        then:
            thrown(IllegalArgumentException)
    }
}
