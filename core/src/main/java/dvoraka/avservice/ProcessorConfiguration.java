package dvoraka.avservice;

import dvoraka.avservice.common.data.AvMessage;

import java.util.Collections;
import java.util.List;
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

        if (inputConditions == null) {
            this.inputConditions = Collections.emptyList();
        } else {
            this.inputConditions = inputConditions;
        }

        this.useOriginalMessage = useOriginalMessage;
    }

    public MessageProcessor getProcessor() {
        return processor;
    }

    public List<Predicate<? super AvMessage>> getInputConditions() {
        return inputConditions;
    }

    public boolean isUseOriginalMessage() {
        return useOriginalMessage;
    }
}
