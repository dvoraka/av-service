package dvoraka.avservice.rest.configuration

import spock.lang.Specification
import spock.lang.Subject

import javax.servlet.Servlet
import javax.servlet.ServletContext
import javax.servlet.ServletRegistration

/**
 * Initializer spec.
 */
class SpringWebInitializerSpec extends Specification {

    @Subject
    SpringWebInitializer webInitializer

    Class<?> configClass = SpringWebConfig.class
    String profilesParam = 'spring.profiles.active'


    def setup() {
        webInitializer = new SpringWebInitializer();
    }


    def "getRootConfigClasses"() {
        expect:
            webInitializer.getRootConfigClasses()
            webInitializer.getRootConfigClasses().length == 1
            webInitializer.getRootConfigClasses()[0] == configClass
    }

    def "getServletConfigClasses"() {
        expect:
            webInitializer.getServletConfigClasses() != null
    }

    def "getServletMappings"() {
        expect:
            webInitializer.getServletMappings()
    }

    def "onStartup without profiles"() {
        given:
            ServletContext servletContext = Mock()
            servletContext.addServlet((String) _, (Servlet) _) >> Mock(ServletRegistration.Dynamic)

        when:
            webInitializer.onStartup(servletContext)

        then:
            1 * servletContext.setInitParameter(profilesParam, (String) _)
    }

    def "onStartup with profiles"() {
        given:
            ServletContext servletContext = Mock()
            servletContext.addServlet((String) _, (Servlet) _) >> Mock(ServletRegistration.Dynamic)

            String profileProperty = "spring.profiles.active"
            System.setProperty(profileProperty, "profiles")

        when:
            webInitializer.onStartup(servletContext)

        then:
            1 * servletContext.setInitParameter(profilesParam, (String) _)

        cleanup:
            System.clearProperty(profileProperty)
    }
}
