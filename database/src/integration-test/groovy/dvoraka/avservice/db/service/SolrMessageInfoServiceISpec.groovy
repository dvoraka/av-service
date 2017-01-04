package dvoraka.avservice.db.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessageSource
import dvoraka.avservice.db.configuration.SolrConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Service test.
 */
@ContextConfiguration(classes = [SolrConfig.class])
@ActiveProfiles(['db-solr'])
class SolrMessageInfoServiceISpec extends Specification {

    @Autowired
    MessageInfoService messageInfoService


    def "save message"() {
        expect:
            messageInfoService.save(Utils.genNormalMessage(), AvMessageSource.TEST, 'TEST-SERVICE')
    }
}
