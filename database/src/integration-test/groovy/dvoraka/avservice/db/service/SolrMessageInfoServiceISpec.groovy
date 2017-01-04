package dvoraka.avservice.db.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.AvMessageInfo
import dvoraka.avservice.common.data.AvMessageSource
import dvoraka.avservice.db.configuration.SolrConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import java.time.Instant
import java.util.stream.Stream

/**
 * Service test.
 */
@ContextConfiguration(classes = [SolrConfig.class])
@ActiveProfiles(['db-solr'])
class SolrMessageInfoServiceISpec extends Specification {

    @Autowired
    MessageInfoService service


    def "save message"() {
        when:
            service.save(Utils.genNormalMessage(), AvMessageSource.TEST, Utils.SERVICE_TEST_ID)

        then:
            notThrown(Exception)
    }

    def "save message and then load info"() {
        given:
            AvMessage message = Utils.genNormalMessage()

        expect:
            service.save(message, AvMessageSource.TEST, Utils.SERVICE_TEST_ID)

        when:
            AvMessageInfo messageInfo = service.loadInfo(message.getId())

        then:
            messageInfo
            messageInfo.getSource() == AvMessageSource.TEST
            messageInfo.getServiceId() == Utils.SERVICE_TEST_ID
    }

    def "save messages and then load info stream"() {
        given:
            AvMessage message = Utils.genNormalMessage()
            int count = 5
            Instant start = Instant.now()

        expect:
            count.times {
                service.save(message, AvMessageSource.TEST, Utils.SERVICE_TEST_ID)
            }

        when:
            Instant afterSave = Instant.now()
            Stream<AvMessageInfo> infoStream = service.loadInfoStream(
                    start,
                    afterSave
            )

        then:
            infoStream
                    .filter { info -> info.getId() == message.getId() }
                    .filter { info -> info.getCreated().isAfter(start) && info.getCreated().isBefore(afterSave) }
                    .count() == count
    }
}
