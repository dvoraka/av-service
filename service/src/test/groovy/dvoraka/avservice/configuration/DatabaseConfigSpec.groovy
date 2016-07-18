package dvoraka.avservice.configuration

import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Database Spring config test.
 */
@ContextConfiguration(classes = [DatabaseConfig.class])
@ActiveProfiles("database")
class DatabaseConfigSpec extends Specification {

    @Autowired
    SessionFactory sessionFactory

    def "SessionFactory loading"() {
        expect:
            sessionFactory
    }
}
