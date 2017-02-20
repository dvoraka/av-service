package dvoraka.avservice;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * Input condition helper.
 */
public final class InputConditions implements BiPredicate<AvMessage, AvMessage> {

    private List<MessageType> allowedOriginalTypes;
//    private boolean originalNonNull;
//    private boolean lastNonNull;


    private InputConditions(Builder builder) {
        allowedOriginalTypes = builder.allowedOriginalTypes;
    }

    @Override
    public boolean test(AvMessage original, AvMessage lastResult) {
        boolean result = allowedOriginalTypes.stream()
                .anyMatch(type -> type.equals(original.getType()));

        return result;
    }

    public static class Builder {

        private List<MessageType> allowedOriginalTypes;


        public Builder() {
            allowedOriginalTypes = new ArrayList<>();
        }

        public Builder originalType(MessageType type) {
            allowedOriginalTypes.add(type);
            return this;
        }

        public InputConditions build() {
            return new InputConditions(this);
        }
    }
}
