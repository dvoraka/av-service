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
        setup:
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
}
