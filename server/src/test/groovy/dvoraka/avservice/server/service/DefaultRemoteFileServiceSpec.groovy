package dvoraka.avservice.server.service

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Subject

/**
 * Remote file service spec.
 */
//TODO
@Ignore('WIP')
class DefaultRemoteFileServiceSpec extends Specification {

    @Subject
    DefaultRemoteFileService service


    def setup() {
        service = null
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
