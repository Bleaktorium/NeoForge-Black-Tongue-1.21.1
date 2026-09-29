package net.bleaktorium.black_tongue.alchemy;

import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class ClientAlchemyKnowledge {
    private static final Set<ResourceLocation> KNOWN = new HashSet<>();

    public static void update(java.util.List<ResourceLocation> items) {
        KNOWN.clear();
        KNOWN.addAll(items);
    }

    public static boolean knows(ResourceLocation itemId) {
        return KNOWN.contains(itemId);
    }
}