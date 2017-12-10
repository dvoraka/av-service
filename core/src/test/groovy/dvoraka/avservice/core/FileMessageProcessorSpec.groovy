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

    FileService fileService
    AvMessageListener listener


    def setup() {
        fileService = Mock()
        listener = Mock()
        processor = new FileMessageProcessor(fileService)
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
            1 * fileService.saveFile(_)
            1 * listener.onMessage(_)
    }

    def "save message failed"() {
        given:
            AvMessage message = Utils.genSaveMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * fileService.saveFile(_) >> {
                throw new FileServiceException()
            }
            1 * listener.onMessage(_)
    }

    def "load message"() {
        given:
            AvMessage message = Utils.genLoadMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * fileService.loadFile(_) >> message
            1 * listener.onMessage(_)
    }

    def "load message failed"() {
        given:
            AvMessage message = Utils.genLoadMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * fileService.loadFile(_) >> {
                throw new FileServiceException()
            }
            1 * listener.onMessage(_)
    }

    def "update message"() {
        given:
            AvMessage message = Utils.genUpdateMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * fileService.updateFile(_)
            1 * listener.onMessage(_)
    }

    def "update message failed"() {
        given:
            AvMessage message = Utils.genUpdateMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * fileService.updateFile(_) >> {
                throw new FileServiceException()
            }
            1 * listener.onMessage(_)
    }

    def "delete message"() {
        given:
            AvMessage message = Utils.genDeleteMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * fileService.deleteFile(_)
            1 * listener.onMessage(_)
    }

    def "delete message failed"() {
        given:
            AvMessage message = Utils.genDeleteMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * fileService.deleteFile(_) >> {
                throw new FileServiceException()
            }
            1 * listener.onMessage(_)
    }

    def "unknown message"() {
        given: "normal message for AV check"
            AvMessage message = Utils.genMessage()

        when:
            processor.sendMessage(message)

        then:
            0 * fileService._
            0 * listener.onMessage(_)
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
            1 * fileService._

        when:
            processor.setInputFilter({ msg -> msg.getType() == MessageType.FILE_LOAD })
            processor.sendMessage(saveMessage)

        then:
            0 * fileService._
    }
}
