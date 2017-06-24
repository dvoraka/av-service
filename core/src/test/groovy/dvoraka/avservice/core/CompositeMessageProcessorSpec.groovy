package dvoraka.avservice.core

import dvoraka.avservice.avprogram.service.AvService
import dvoraka.avservice.common.AvMessageListener
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.db.service.MessageInfoService
import dvoraka.avservice.storage.service.FileService
import spock.lang.Specification
import spock.lang.Subject

/**
 * CompositeMessageProcessor spec.
 */
class CompositeMessageProcessorSpec extends Specification {

    @Subject
    CompositeMessageProcessor processor

    AvService avService
    FileService fileService

    AvMessageListener listener


    def setup() {
        processor = new CompositeMessageProcessor()

        avService = Mock()

        fileService = Mock()
        fileService.loadFile(_) >> Utils.genFileMessage()

        listener = Mock()

        MessageProcessor checkMessageProcessor = new AvCheckMessageProcessor(
                4,
                'test',
                avService,
                Mock(MessageInfoService)
        )
        MessageProcessor fileMessageProcessor = new FileMessageProcessor(fileService)

        List<InputConditions> checkConditions =
                new InputConditions.Builder()
                        .originalType(MessageType.FILE_CHECK)
                        .originalType(MessageType.FILE_SAVE)
                        .originalType(MessageType.FILE_UPDATE)
                        .build().toList()

        List<InputConditions> fileSaveUpdateConditions =
                new InputConditions.Builder()
                        .originalType(MessageType.FILE_SAVE)
                        .originalType(MessageType.FILE_UPDATE)
                        .condition(
                        { orig, last ->
                            (last.getVirusInfo() != null
                                    && last.getVirusInfo() == Utils.OK_VIRUS_INFO)
                        })
                        .build().toList()

        List<InputConditions> fileLoadDeleteConditions =
                new InputConditions.Builder()
                        .originalType(MessageType.FILE_LOAD)
                        .originalType(MessageType.FILE_DELETE)
                        .build().toList()

        ProcessorConfiguration checkConfig = new ProcessorConfiguration(
                checkMessageProcessor,
                checkConditions,
                true
        )
        ProcessorConfiguration fileSaveUpdateConfig = new ProcessorConfiguration(
                fileMessageProcessor,
                fileSaveUpdateConditions,
                true
        )
        ProcessorConfiguration fileLoadDeleteConfig = new ProcessorConfiguration(
                fileMessageProcessor,
                fileLoadDeleteConditions,
                true
        )

        processor.addProcessor(checkConfig)
        processor.addProcessor(fileSaveUpdateConfig)
        processor.addProcessor(fileLoadDeleteConfig)

        processor.addProcessedAVMessageListener(listener)

        processor.start()
    }

    def cleanup() {
        processor.stop()
        processor.removeProcessedAVMessageListener(listener)
    }

    def "send message for check"() {
        given:
            AvMessage message = Utils.genMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * avService._
            1 * listener.onAvMessage(_)

            0 * fileService._
    }

    def "send save message"() {
        given:
            AvMessage message = Utils.genSaveMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * avService._ >> Utils.OK_VIRUS_INFO
            1 * listener.onAvMessage(_)

            1 * fileService.saveFile(_)
            1 * listener.onAvMessage(_)
    }

    def "send update message"() {
        given:
            AvMessage message = Utils.genUpdateMessage()

        when:
            processor.sendMessage(message)

        then:
            1 * avService._ >> Utils.OK_VIRUS_INFO
            1 * listener.onAvMessage(_)

            1 * fileService.updateFile(_)
            1 * listener.onAvMessage(_)
    }

    def "send load message"() {
        given:
            AvMessage message = Utils.genLoadMessage()

        when:
            processor.sendMessage(message)

        then:
            0 * avService._

            1 * fileService.loadFile(_) >> Utils.genFileMessage()
            1 * listener.onAvMessage(_)
    }

    def "send delete message"() {
        given:
            AvMessage message = Utils.genDeleteMessage()

        when:
            processor.sendMessage(message)

        then:
            0 * avService._

            1 * fileService.deleteFile(_)
            1 * listener.onAvMessage(_)
    }

    def "message status for unknown ID"() {
        expect:
            processor.messageStatus('XXX') == MessageStatus.UNKNOWN
    }

    def "set input filter"() {
        given:
            AvMessage saveMessage = Utils.genSaveMessage()

        when:
            processor.sendMessage(saveMessage)

        then:
            1 * listener.onAvMessage(_)

        when:
            processor.setInputFilter({ msg -> msg.getType() == MessageType.FILE_LOAD })
            processor.sendMessage(saveMessage)

        then:
            0 * listener.onAvMessage(_)
    }
}
