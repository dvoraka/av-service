package dvoraka.avservice

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.db.service.MessageInfoService
import dvoraka.avservice.service.AvService
import dvoraka.avservice.storage.service.FileService
import spock.lang.Ignore
import spock.lang.Specification

import java.util.function.Predicate

/**
 * CompositeMessageProcessor spec.
 */
@Ignore('WIP')
class CompositeMessageProcessorSpec extends Specification {

    def "test"() {
        given:
            AvCheckMessageProcessor processor1 = new AvCheckMessageProcessor(
                    1, "test", Mock(AvService), Mock(MessageInfoService)
            )
            FileMessageProcessor processor2 = new FileMessageProcessor(Mock(FileService))
            FileMessageProcessor processor3 = new FileMessageProcessor(Mock(FileService))

            ProcessorConfiguration configuration1 = new ProcessorConfiguration(processor1)
            ProcessorConfiguration configuration2 = new ProcessorConfiguration(processor2)

            List<Predicate<? super AvMessage>> conditions = new ArrayList<>()
            conditions.add(new Predicate<AvMessage>() {
                @Override
                boolean test(AvMessage message) {
                    return message.getOwner() != null
                }
            })

            ProcessorConfiguration configuration3 = new ProcessorConfiguration(
                    processor3,
                    conditions,
                    false)

            CompositeMessageProcessor processor = new CompositeMessageProcessor()
            processor.addProcessor(configuration1)
            processor.addProcessor(configuration2)
            processor.addProcessor(configuration1)
            processor.addProcessor(configuration3)

        expect:
            processor.start()
            processor.sendMessage(Utils.genFileMessage())
    }
}
