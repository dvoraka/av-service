package dvoraka.avservice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Specification class for AVService.
 */
@ContextConfiguration(classes = [AppConfig])
class AVServiceSpec extends Specification {

    @Autowired
    AVService avService;

    def "default test"() {
        expect:
        true
    }

    def "AV service loading"() {
        expect:
        avService != null
    }
}
