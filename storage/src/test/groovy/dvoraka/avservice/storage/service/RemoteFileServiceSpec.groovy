package dvoraka.avservice.storage.service

import spock.lang.Specification
import spock.lang.Subject

/**
 * Remote file service spec.
 */
//TODO
class RemoteFileServiceSpec extends Specification {

    @Subject
    RemoteFileService service

    FileService fileService


    def setup() {
        fileService = Mock()
        service = new RemoteFileService(fileService)
    }

    def "save file"() {
        expect:
            service.saveFile(null)
    }

    def "load file"() {
        expect:
            service.loadFile(null) == null
    }

    def "update file"() {
        expect:
            service.updateFile(null)
    }

    def "delete file"() {
        expect:
            service.deleteFile(null)
    }
}
