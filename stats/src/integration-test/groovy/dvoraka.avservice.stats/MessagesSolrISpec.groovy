package dvoraka.avservice.stats

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.AvMessageInfo
import dvoraka.avservice.common.data.AvMessageSource
import dvoraka.avservice.db.service.MessageInfoService
import dvoraka.avservice.stats.configuration.StatsSolrConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import java.time.Instant
import java.util.stream.Stream

/**
 * Solr messages spec.
 */
@ContextConfiguration(classes = [StatsSolrConfig.class])
@ActiveProfiles(['stats-solr', 'db-solr'])
class MessagesSolrISpec extends Specification {

    @Autowired
    Messages messages
    @Autowired
    MessageInfoService infoService

    AvMessageSource source
    String serviceId


    def setup() {
        source = AvMessageSource.TEST
        serviceId = Utils.SERVICE_TEST_ID
    }

    def "save and load message"() {
        given:
            AvMessage message = Utils.genNormalMessage()
            Instant start = Instant.now()

        expect:
            infoService.save(message, source, serviceId)
            Instant afterSave = Instant.now()

        when:
            Stream<AvMessageInfo> infos = messages.when(start, afterSave)

        then:
            infos
                    .filter { info -> info.getId() == message.getId() }
                    .count() == 1
    }
}
