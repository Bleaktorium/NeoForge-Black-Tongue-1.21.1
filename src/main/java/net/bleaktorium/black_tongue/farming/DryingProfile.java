package net.bleaktorium.black_tongue.farming;

import net.bleaktorium.black_tongue.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record DryingProfile(List<ItemStack> stageResults) {
    public int stageCount() {
        return stageResults.size();
    }
}

