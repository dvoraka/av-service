package dvoraka.avservice.core;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * Input condition helper.
 */
public final class InputConditions implements BiPredicate<AvMessage, AvMessage> {

    private final List<MessageType> allowedOriginalTypes;
    private final List<MessageType> allowedLastTypes;
    private final List<BiPredicate<AvMessage, AvMessage>> conditions;


    private InputConditions(Builder builder) {
        allowedOriginalTypes = builder.allowedOriginalTypes;
        allowedLastTypes = builder.allowedLastTypes;
        conditions = builder.conditions;
    }

    @Override
    public boolean test(AvMessage original, AvMessage lastResult) {

        boolean originalTypes = allowedOriginalTypes.isEmpty() || allowedOriginalTypes.stream()
                .anyMatch(type -> type.equals(original.getType()));

        boolean lastTypes = allowedLastTypes.isEmpty() || allowedLastTypes.stream()
                .anyMatch(type -> type.equals(lastResult.getType()));

        boolean conditionsPassed = conditions.isEmpty() || conditions.stream()
                .allMatch(predicate -> predicate.test(original, lastResult));

        return originalTypes && lastTypes && conditionsPassed;
    }

    public List<InputConditions> toList() {
        List<InputConditions> list = new ArrayList<>();
        list.add(this);

        return list;
    }

    public static class Builder {

        private List<MessageType> allowedOriginalTypes;
        private List<MessageType> allowedLastTypes;
        private List<BiPredicate<AvMessage, AvMessage>> conditions;


        public Builder() {
            allowedOriginalTypes = new ArrayList<>();
            allowedLastTypes = new ArrayList<>();
            conditions = new ArrayList<>();
        }

        public Builder originalType(MessageType type) {
            allowedOriginalTypes.add(type);
            return this;
        }

        public Builder lastType(MessageType type) {
            allowedLastTypes.add(type);
            return this;
        }

        public Builder condition(BiPredicate<AvMessage, AvMessage> cond) {
            conditions.add(cond);
            return this;
        }

        public InputConditions build() {
            return new InputConditions(this);
        }
    }
}
