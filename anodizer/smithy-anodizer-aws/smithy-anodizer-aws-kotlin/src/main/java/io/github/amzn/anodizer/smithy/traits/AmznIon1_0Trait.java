package io.github.amzn.anodizer.smithy.traits;

import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.AbstractTrait;

/**
 * An RPC protocol that sends Ion encoded payloads.
 */
public class AmznIon1_0Trait extends AmznProtocolTrait {

    public static final ShapeId ID = ShapeId.from("amzn.protocols#amznIon1_0");

    private AmznIon1_0Trait(Builder builder) {
        super(ID, builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends AmznProtocolTrait.Builder<AmznIon1_0Trait, Builder> {
        private Builder() {
        }

        @Override
        public AmznIon1_0Trait build() {
            return new AmznIon1_0Trait(this);
        }
    }

    public static final class Provider extends AbstractTrait.Provider {
        public Provider() {
            super(ID);
        }

        @Override
        public AmznIon1_0Trait createTrait(ShapeId target, Node value) {
            AmznIon1_0Trait result = builder().fromNode(value).build();
            result.setNodeCache(value);
            return result;
        }
    }
}
