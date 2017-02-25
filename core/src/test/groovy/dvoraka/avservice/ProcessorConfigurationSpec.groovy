package dvoraka.avservice

import spock.lang.Specification
import spock.lang.Subject

/**
 * Processor configuration spec.
 */
class ProcessorConfigurationSpec extends Specification {

    @Subject
    ProcessorConfiguration configuration

    MessageProcessor messageProcessor


    def setup() {
        messageProcessor = Mock()
        configuration = new ProcessorConfiguration(messageProcessor)
    }

    def "check constructor (Message processor)"() {
        expect:
            configuration.getProcessor() != null
            configuration.getInputConditions().isEmpty()
            configuration.isUseOriginalMessage()
    }

    def "check constructor"() {
        setup:
            MessageProcessor processor = Mock()
            List<InputConditions> inputConditions = Mock()
            configuration = new ProcessorConfiguration(processor, inputConditions, false)

        expect:
            configuration.getProcessor() == processor
            configuration.getInputConditions() == inputConditions
            !configuration.isUseOriginalMessage()
    }

    def "to string"() {
        expect:
            configuration.toString().endsWith('}')
    }
}
