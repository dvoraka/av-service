package dvoraka.avservice.configuration


import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Database Spring config test.
 */
@ContextConfiguration(classes = [DatabaseConfig.class])
@ActiveProfiles("database")
class DatabaseConfigISpec extends Specification {

    @Autowired
    SessionFactory sessionFactory
    @Autowired
    CarService carService


    def "SessionFactory loading"() {
        expect:
            sessionFactory
    }

    @Ignore
    def "get Car table size"() {
        when:
            carService.count()

        then:
            notThrown(Exception)
    }
}
