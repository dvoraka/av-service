package dvoraka.avservice.common.service

import spock.lang.Specification
import spock.lang.Subject

class Md5HashingServiceSpec extends Specification {

    @Subject
    Md5HashingService service


    def setup() {
        service = new Md5HashingService()
    }

    def "array hash"() {
        given:
            byte[] bytes = new byte[10]

        when:
            String result = service.arrayHash(bytes)

        then:
            result
    }

    def "string hash"() {
        given:
            String str = 'test string'

        when:
            String result = service.stringHash(str)

        then:
            result
    }
}
