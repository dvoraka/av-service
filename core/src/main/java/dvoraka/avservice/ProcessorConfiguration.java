package dvoraka.avservice;

import dvoraka.avservice.common.data.AvMessage;

import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * Message processor configuration for composite processor.
 */
public class ProcessorConfiguration {

    private final MessageProcessor processor;
    private final Predicate<? super AvMessage> inputCondition;
    private final boolean useOriginalMessage;


    public ProcessorConfiguration(MessageProcessor processor) {
        this(processor, null, true);
    }

    public ProcessorConfiguration(
            MessageProcessor processor,
            Predicate<? super AvMessage> inputCondition,
            boolean useOriginalMessage
    ) {
        this.processor = requireNonNull(processor);
        this.inputCondition = inputCondition;
        this.useOriginalMessage = useOriginalMessage;
    }

    public MessageProcessor getProcessor() {
        return processor;
    }

    public Optional<Predicate<? super AvMessage>> getInputCondition() {
        return Optional.ofNullable(inputCondition);
    }
}
