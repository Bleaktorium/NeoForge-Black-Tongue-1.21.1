package net.bleaktorium.black_tongue.coven;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public class ClientCovenQuestState {
    private static boolean active = false;
    private static List<ResourceLocation> assignedPotions = List.of();

    public static void update(boolean isActive, List<ResourceLocation> potions) {
        active = isActive;
        assignedPotions = potions;
    }

    public static boolean isActive() { return active; }
    public static List<ResourceLocation> getAssignedPotions() { return assignedPotions; }
}