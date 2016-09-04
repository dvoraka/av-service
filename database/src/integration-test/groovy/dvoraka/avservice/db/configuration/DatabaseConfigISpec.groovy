package dvoraka.avservice.db.configuration

import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Database Spring config test.
 */
@ContextConfiguration(classes = [DatabaseConfig.class])
@ActiveProfiles("database")
class DatabaseConfigISpec extends Specification {
}
