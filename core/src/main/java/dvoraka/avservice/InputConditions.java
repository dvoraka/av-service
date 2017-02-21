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
    private List<MessageType> allowedLastTypes;
//    private boolean originalNonNull;
//    private boolean lastNonNull;


    private InputConditions(Builder builder) {
        allowedOriginalTypes = builder.allowedOriginalTypes;
        allowedLastTypes = builder.allowedLastTypes;
    }

    @Override
    public boolean test(AvMessage original, AvMessage lastResult) {
        boolean originalTypes =
                allowedOriginalTypes.size() <= 0 || allowedOriginalTypes.stream()
                        .anyMatch(type -> type.equals(original.getType()));

        boolean lastTypes =
                allowedLastTypes.size() <= 0 || allowedLastTypes.stream()
                        .anyMatch(type -> type.equals(lastResult.getType()));

        return originalTypes && lastTypes;
    }

    public static class Builder {

        private List<MessageType> allowedOriginalTypes;
        private List<MessageType> allowedLastTypes;


        public Builder() {
            allowedOriginalTypes = new ArrayList<>();
            allowedLastTypes = new ArrayList<>();
        }

        public Builder originalType(MessageType type) {
            allowedOriginalTypes.add(type);
            return this;
        }

        public Builder lastType(MessageType type) {
            allowedLastTypes.add(type);
            return this;
        }

        public InputConditions build() {
            return new InputConditions(this);
        }
    }
}
