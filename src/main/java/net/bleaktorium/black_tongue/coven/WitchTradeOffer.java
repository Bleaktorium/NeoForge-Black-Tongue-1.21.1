package net.bleaktorium.black_tongue.coven;

import net.bleaktorium.black_tongue.item.ModItems;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record WitchTradeOffer(List<ItemStack> inputs, ItemStack output, int requiredReputation, boolean isAmuletReward) {

    public static WitchTradeOffer of(int requiredReputation, ItemStack output, ItemStack... inputs) {
        return new WitchTradeOffer(List.of(inputs), output, requiredReputation, false);
    }

    public static WitchTradeOffer amuletReward(int requiredReputation, ItemStack... inputs) {
        return new WitchTradeOffer(List.of(inputs), ItemStack.EMPTY, requiredReputation, true);
    }

    public ItemStack resolveOutput(String witchName, @Nullable UUID soulId) {
        if (isAmuletReward) {
            ItemStack amulet = new ItemStack(ModItems.COVEN_SUMMONING_AMULET.get());
            amulet.set(ModDataComponents.AMULET_BINDING.get(),
                    new AmuletBinding(SummonedWitchType.COVENLESS_WITCH, Optional.of(witchName), Optional.ofNullable(soulId)));
            return amulet;
        }
        return output.copy();
    }

    public boolean matches(ItemStack input0, ItemStack input1) {
        if (inputs.isEmpty()) return false;

        ItemStack required0 = inputs.get(0);
        if (!ItemStack.isSameItemSameComponents(input0, required0) || input0.getCount() < required0.getCount()) {
            return false;
        }

        if (inputs.size() < 2) {
            return true;
        }

        ItemStack required1 = inputs.get(1);
        return ItemStack.isSameItemSameComponents(input1, required1) && input1.getCount() >= required1.getCount();
    }
}