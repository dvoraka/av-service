package dvoraka.avservice

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.storage.service.FileService
import spock.lang.Specification
import spock.lang.Subject

/**
 * File message processor spec.
 */
class FileMessageProcessorSpec extends Specification {

    @Subject
    FileMessageProcessor processor

    FileService service


    def setup() {
        service = Mock()
        processor = new FileMessageProcessor(service)
    }

    def "save message"() {
        given:
            AvMessage message = Utils.genFileMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * service.saveFile(message)
    }
}
