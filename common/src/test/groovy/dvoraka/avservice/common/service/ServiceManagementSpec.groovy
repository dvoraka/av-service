package dvoraka.avservice.common.service

import spock.lang.Specification
import spock.lang.Subject

/**
 * ServiceManagementSpec
 */
class ServiceManagementSpec extends Specification {

    @Subject
    ServiceManagement serviceManagement


    def setup() {
        serviceManagement = Spy()
    }

    def "test restart method"() {
        when:
            serviceManagement.restart()

        then:
            1 * serviceManagement.stop() >> {}
            1 * serviceManagement.start() >> {}
    }
}
