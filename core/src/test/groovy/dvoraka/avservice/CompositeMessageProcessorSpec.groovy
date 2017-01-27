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

            CompositeMessageProcessor processor = new CompositeMessageProcessor()
            processor.addProcessor(processor2)
            processor.addProcessor(processor1)


        expect:
            processor.start()
            processor.sendMessage(Utils.genFileMessage())
    }
}
