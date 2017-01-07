package dvoraka.avservice.common.data

import dvoraka.avservice.common.Utils
import spock.lang.Specification

import java.time.Instant

/**
 * Default message info spec.
 */
class DefaultAvMessageInfoSpec extends Specification {

    def "build message"() {
        given:
            String uuid = Utils.genUuidString()
            Instant now = Instant.now()
            DefaultAvMessageInfo messageInfo =
                    new DefaultAvMessageInfo.Builder(uuid)
                            .source(AvMessageSource.TEST)
                            .serviceId(Utils.SERVICE_TEST_ID)
                            .created(now)
                            .build()

        expect:
            messageInfo.getId() == uuid
            messageInfo.getSource() == AvMessageSource.TEST
            messageInfo.getServiceId() == Utils.SERVICE_TEST_ID
            messageInfo.getCreated() == now
    }
}
