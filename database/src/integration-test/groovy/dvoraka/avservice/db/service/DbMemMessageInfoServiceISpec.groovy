package dvoraka.avservice.db.service

import dvoraka.avservice.db.configuration.DbMemConfig
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * In-memory DB Service spec.
 */
@ContextConfiguration(classes = [DbMemConfig.class])
@ActiveProfiles(['db-mem'])
class DbMemMessageInfoServiceISpec extends MessageInfoServiceISpec {
}
