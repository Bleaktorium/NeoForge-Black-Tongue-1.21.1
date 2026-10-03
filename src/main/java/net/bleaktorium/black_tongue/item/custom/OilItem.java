package net.bleaktorium.black_tongue.item.custom;

import net.bleaktorium.black_tongue.remains.RemainsData;
import net.minecraft.world.item.Item;

public class OilItem extends Item {
    private final RemainsData.DecayTier tier;

    public OilItem(RemainsData.DecayTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public RemainsData.DecayTier tier() {
        return tier;
    }
}