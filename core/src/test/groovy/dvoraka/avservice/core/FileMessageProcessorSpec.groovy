package dvoraka.avservice.core

import dvoraka.avservice.common.AvMessageListener
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.storage.exception.FileServiceException
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

    def cleanup() {
        processor.removeProcessedAVMessageListener(listener)
    }

    def "save message"() {
        given:
            AvMessage message = Utils.genSaveMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * service.saveFile(_)
            1 * listener.onAvMessage(_)
    }

    def "save message failed"() {
        given:
            AvMessage message = Utils.genSaveMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * service.saveFile(_) >> {
                throw new FileServiceException()
            }
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

    def "load message failed"() {
        given:
            AvMessage message = Utils.genLoadMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * service.loadFile(_) >> {
                throw new FileServiceException()
            }
            1 * listener.onAvMessage(_)
    }

    def "update message"() {
        given:
            AvMessage message = Utils.genUpdateMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * service.updateFile(_)
            1 * listener.onAvMessage(_)
    }

    def "update message failed"() {
        given:
            AvMessage message = Utils.genUpdateMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * service.updateFile(_) >> {
                throw new FileServiceException()
            }
            1 * listener.onAvMessage(_)
    }

    def "delete message"() {
        given:
            AvMessage message = Utils.genDeleteMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * service.deleteFile(_)
            1 * listener.onAvMessage(_)
    }

    def "delete message failed"() {
        given:
            AvMessage message = Utils.genDeleteMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * service.deleteFile(_) >> {
                throw new FileServiceException()
            }
            1 * listener.onAvMessage(_)
    }

    def "unknown message"() {
        given: "normal message for AV check"
            AvMessage message = Utils.genMessage()

        when:
            processor.sendMessage(message)

        then:
            0 * service._
            0 * listener.onAvMessage(_)
    }

    def "message status for unknown message"() {
        expect:
            processor.messageStatus('XXX') == MessageStatus.UNKNOWN
    }

    def "message status"() {
        given: "normal message for AV check"
            AvMessage message = Utils.genMessage()

        when:
            processor.sendMessage(message)

        then:
            processor.messageStatus(message.getId()) == MessageStatus.PROCESSED
    }

    def "start and stop processor"() {
        when:
            processor.start()
            processor.stop()

        then:
            notThrown(Exception)
            0 * _
    }

    def "set input filter"() {
        given:
            AvMessage saveMessage = Utils.genSaveMessage()

        when:
            processor.sendMessage(saveMessage)

        then:
            1 * service._

        when:
            processor.setInputFilter({ msg -> msg.getType() == MessageType.FILE_LOAD })
            processor.sendMessage(saveMessage)

        then:
            0 * service._
    }
}
