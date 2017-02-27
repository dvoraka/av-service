package dvoraka.avservice

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.db.service.MessageInfoService
import dvoraka.avservice.service.AvService
import dvoraka.avservice.storage.service.FileService
import spock.lang.Specification
import spock.lang.Subject

/**
 * CompositeMessageProcessor spec.
 */
class CompositeMessageProcessorSpec extends Specification {

    @Subject
    CompositeMessageProcessor processor

    FileService fileService


    def setup() {
        processor = new CompositeMessageProcessor()

        fileService = Mock()
        fileService.loadFile(_) >> Utils.genFileMessage()

        MessageProcessor checkMessageProcessor = new AvCheckMessageProcessor(
                4,
                'test',
                Mock(AvService),
                Mock(MessageInfoService)
        )
        MessageProcessor fileMessageProcessor = new FileMessageProcessor(fileService)

        List<InputConditions> checkConditions =
                new InputConditions.Builder()
                        .originalType(MessageType.REQUEST)
                        .originalType(MessageType.FILE_SAVE)
                        .originalType(MessageType.FILE_UPDATE)
                        .build().toList()

        List<InputConditions> fileSaveUpdateConditions =
                new InputConditions.Builder()
                        .originalType(MessageType.FILE_SAVE)
                        .originalType(MessageType.FILE_UPDATE)
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

        processor.start()
    }

    def "send load message"() {
        given:
            AvMessage message = Utils.genLoadMessage()

        expect:
            processor.sendMessage(message)
    }
}
