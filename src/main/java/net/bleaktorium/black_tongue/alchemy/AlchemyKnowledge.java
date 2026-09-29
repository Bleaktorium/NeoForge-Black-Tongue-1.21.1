package net.bleaktorium.black_tongue.alchemy;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public record AlchemyKnowledge(Set<ResourceLocation> knownItems) {

    public static final Codec<AlchemyKnowledge> CODEC = ResourceLocation.CODEC.listOf()
            .xmap(list -> new AlchemyKnowledge(new HashSet<>(list)), knowledge -> knowledge.knownItems().stream().toList());

    public static AlchemyKnowledge initial() {
        return new AlchemyKnowledge(new HashSet<>());
    }

    public boolean knows(ResourceLocation itemId) {
        return knownItems.contains(itemId);
    }

    public AlchemyKnowledge withLearned(ResourceLocation itemId) {
        Set<ResourceLocation> updated = new HashSet<>(knownItems);
        updated.add(itemId);
        return new AlchemyKnowledge(updated);
    }
}