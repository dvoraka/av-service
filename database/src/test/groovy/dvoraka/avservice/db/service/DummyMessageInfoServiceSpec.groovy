package dvoraka.avservice.db.service

import spock.lang.Specification

/**
 * Service test.
 */
class DummyMessageInfoServiceSpec extends Specification {

    DummyMessageInfoService infoService


    def setup() {
        infoService = new DummyMessageInfoService()
    }

    def "save calling"() {
        expect:
            infoService.save(null, null, null)
    }

    def "message info should be null"() {
        expect:
            infoService.loadInfo(null) == null
    }

    def "info stream should be empty"() {
        expect:
            infoService.loadInfoStream(null, null).count() == 0
    }
}
