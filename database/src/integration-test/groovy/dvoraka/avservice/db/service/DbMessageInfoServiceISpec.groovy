package dvoraka.avservice.db.service

import dvoraka.avservice.db.configuration.DbConfig
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * DB Service spec.
 */
@ContextConfiguration(classes = [DbConfig.class])
@ActiveProfiles(['db'])
class DbMessageInfoServiceISpec extends MessageInfoServiceISpec {
}
