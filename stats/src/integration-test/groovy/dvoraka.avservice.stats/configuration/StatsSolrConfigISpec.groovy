package dvoraka.avservice.stats.configuration

import dvoraka.avservice.stats.StatsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [StatsConfig.class])
@ActiveProfiles(['stats', 'stats-solr', 'db-solr'])
class StatsSolrConfigISpec extends Specification {

    @Autowired
    StatsService service


    def "test"() {
        expect:
            true
    }
}
