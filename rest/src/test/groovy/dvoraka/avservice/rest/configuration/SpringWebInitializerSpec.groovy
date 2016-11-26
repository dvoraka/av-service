package dvoraka.avservice.rest.configuration

import spock.lang.Specification
import spock.lang.Subject

/**
 * Initializer spec.
 */
class SpringWebInitializerSpec extends Specification {

    @Subject
    SpringWebInitializer webInitializer

    Class<?> configClass = SpringWebConfig.class


    def setup() {
        webInitializer = new SpringWebInitializer();
    }


    def "GetRootConfigClasses"() {
        expect:
            webInitializer.getRootConfigClasses()
            webInitializer.getRootConfigClasses().length == 1
            webInitializer.getRootConfigClasses()[0] == configClass
    }

    def "GetServletConfigClasses"() {
        expect:
            webInitializer.getServletConfigClasses() != null
    }

    def "GetServletMappings"() {
        expect:
            webInitializer.getServletMappings()
    }
}
