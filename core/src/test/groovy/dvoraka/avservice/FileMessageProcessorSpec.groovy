package dvoraka.avservice

import dvoraka.avservice.common.AvMessageListener
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
    AvMessageListener listener


    def setup() {
        service = Mock()
        listener = Mock()
        processor = new FileMessageProcessor(service)
        processor.addProcessedAVMessageListener(listener)
    }

    def "save message"() {
        given:
            AvMessage message = Utils.genSaveMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * service.saveFile(message)
            1 * listener.onAvMessage(_)
    }

    def "load message"() {
        given:
            AvMessage message = Utils.genLoadMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * service.loadFile(_) >> message
            1 * listener.onAvMessage(_)
    }
}
