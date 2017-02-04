package dvoraka.avservice;

import dvoraka.avservice.common.data.AvMessage;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * Message processor configuration for a composite processor.
 */
public class ProcessorConfiguration {

    private final MessageProcessor processor;
    private final List<Predicate<? super AvMessage>> inputConditions;
    private final boolean useOriginalMessage;


    public ProcessorConfiguration(MessageProcessor processor) {
        this(processor, null, true);
    }

    public ProcessorConfiguration(
            MessageProcessor processor,
            List<Predicate<? super AvMessage>> inputConditions,
            boolean useOriginalMessage
    ) {
        this.processor = requireNonNull(processor);
        this.inputConditions = inputConditions;
        this.useOriginalMessage = useOriginalMessage;
    }

    public MessageProcessor getProcessor() {
        return processor;
    }

    public Optional<List<Predicate<? super AvMessage>>> getInputConditions() {
        return Optional.ofNullable(inputConditions);
    }

    public boolean isUseOriginalMessage() {
        return useOriginalMessage;
    }
}
