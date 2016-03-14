package dvoraka.avservice;

import spock.lang.Specification;

/**
 * Integration test test.
 */
public class TestISpec extends Specification {

    def "template"() {
        setup:
        print("Integration test")

        expect:
        true
    }
}
