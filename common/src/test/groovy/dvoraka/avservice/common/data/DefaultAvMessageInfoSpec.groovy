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
                            .source(InfoSource.TEST)
                            .serviceId(Utils.TEST_SERVICE_ID)
                            .created(now)
                            .build()

        expect:
            messageInfo.getId() == uuid
            messageInfo.getSource() == InfoSource.TEST
            messageInfo.getServiceId() == Utils.TEST_SERVICE_ID
            messageInfo.getCreated() == now
    }
}
