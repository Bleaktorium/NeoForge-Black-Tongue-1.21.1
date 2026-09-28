package net.bleaktorium.black_tongue.ritual;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public class RitualIngredients {
    public static boolean isValidOffering(ItemStack stack) {
        return !stack.isEmpty() && stack.has(DataComponents.FOOD);
    }
}