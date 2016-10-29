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

    def "message info should be null"() {
        expect:
            infoService.getMessageInfo(null) == null
    }
}
