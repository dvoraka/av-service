package dvoraka.avservice.db.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.AvMessageInfo
import dvoraka.avservice.common.data.AvMessageSource
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Specification

import java.time.Instant
import java.util.stream.Stream

/**
 * Message info service spec.
 */
@Ignore
class MessageInfoServiceISpec extends Specification {

    @Autowired
    MessageInfoService service


    def "save message"() {
        expect:
            service.save(Utils.genNormalMessage(), AvMessageSource.TEST, 'TEST-SERVICE')
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
