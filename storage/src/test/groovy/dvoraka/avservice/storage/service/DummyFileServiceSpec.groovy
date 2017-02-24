package dvoraka.avservice.storage.service

import spock.lang.Specification
import spock.lang.Subject

/**
 * Dummy file service spec.
 */
class DummyFileServiceSpec extends Specification {

    @Subject
    DummyFileService service


    def setup() {
        service = new DummyFileService()
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
            service.updateFile(null)
    }
}
