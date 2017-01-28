package dvoraka.avservice

import dvoraka.avservice.common.Utils
import dvoraka.avservice.db.service.MessageInfoService
import dvoraka.avservice.service.AvService
import dvoraka.avservice.storage.service.FileService
import spock.lang.Ignore
import spock.lang.Specification

/**
 * CompositeMessageProcessor spec.
 */
@Ignore('WIP')
class CompositeMessageProcessorSpec extends Specification {

    def "test"() {
        given:
            DefaultMessageProcessor processor1 = new DefaultMessageProcessor(
                    1, "test", Mock(AvService), Mock(MessageInfoService)
            )
            FileMessageProcessor processor2 = new FileMessageProcessor(Mock(FileService))

            ProcessorConfiguration configuration1 = new ProcessorConfiguration(processor1)
            ProcessorConfiguration configuration2 = new ProcessorConfiguration(processor2)

            CompositeMessageProcessor processor = new CompositeMessageProcessor()
            processor.addProcessor(configuration1)
            processor.addProcessor(configuration2)

        expect:
            processor.start()
            processor.sendMessage(Utils.genFileMessage())
    }
}
