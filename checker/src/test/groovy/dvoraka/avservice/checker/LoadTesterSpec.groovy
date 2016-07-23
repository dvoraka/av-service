package dvoraka.avservice.checker

import spock.lang.Specification

/**
 * Load tester spec.
 */
class LoadTesterSpec extends Specification {

    LoadTester loadTester


    def "constructor"() {
        when:
            loadTester = new LoadTester(null)

        then:
            loadTester.getProps()
    }
}
