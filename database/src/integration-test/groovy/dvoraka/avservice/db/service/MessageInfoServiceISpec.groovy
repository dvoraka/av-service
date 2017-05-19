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
@Ignore('base class')
class MessageInfoServiceISpec extends Specification {

    @Autowired
    MessageInfoService service


    def "save message"() {
        expect:
            service.save(Utils.genMessage(), AvMessageSource.TEST, 'TEST-SERVICE')
    }

    def "save message and then load info"() {
        given:
            AvMessage message = Utils.genMessage()

        expect:
            service.save(message, AvMessageSource.TEST, Utils.TEST_SERVICE_ID)

        when:
            AvMessageInfo messageInfo = service.loadInfo(message.getId())

        then:
            messageInfo
            messageInfo.getSource() == AvMessageSource.TEST
            messageInfo.getServiceId() == Utils.TEST_SERVICE_ID
    }

    def "save messages and then load info stream"() {
        given:
            AvMessage message = Utils.genMessage()
            int count = 5
            Instant start = Instant.now().minusMillis(10) // start must be before save

        expect:
            count.times {
                service.save(message, AvMessageSource.TEST, Utils.TEST_SERVICE_ID)
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
