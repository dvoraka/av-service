package dvoraka.avservice.storage.service

import dvoraka.avservice.storage.configuration.StorageConfig
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * In-memory DB file service spec.
 */
@ContextConfiguration(classes = [StorageConfig.class])
@ActiveProfiles(['storage', 'db-mem'])
class DbMemFileServiceISpec extends FileServiceISpec {
}
