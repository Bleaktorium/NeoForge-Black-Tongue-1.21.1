package net.bleaktorium.black_tongue.coven;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class WitchTradePool {

    private static final Map<String, List<WitchTradeOffer>> POOLS = Map.of(
            "Yennefer", List.of(
                    WitchTradeOffer.of(0, new ItemStack(Items.EMERALD), new ItemStack(Items.STRING, 8)),
                    WitchTradeOffer.of(10, new ItemStack(Items.EMERALD), new ItemStack(Items.GLASS_BOTTLE, 4)),
                    WitchTradeOffer.of(25, new ItemStack(Items.EMERALD, 2), new ItemStack(Items.SPIDER_EYE, 2)),
                    WitchTradeOffer.amuletReward(50, new ItemStack(Items.EMERALD, 10))
            ),
            "Doedre", List.of(
                    WitchTradeOffer.of(0, new ItemStack(Items.EMERALD), new ItemStack(Items.BONE, 6)),
                    WitchTradeOffer.of(10, new ItemStack(Items.EMERALD), new ItemStack(Items.GUNPOWDER, 3)),
                    WitchTradeOffer.of(25, new ItemStack(Items.EMERALD, 2), new ItemStack(Items.FERMENTED_SPIDER_EYE)),
                    WitchTradeOffer.amuletReward(50, new ItemStack(Items.EMERALD, 10))
            ),
            "Marina", List.of(
                    WitchTradeOffer.of(0, new ItemStack(Items.EMERALD), new ItemStack(Items.KELP, 8)),
                    WitchTradeOffer.of(10, new ItemStack(Items.EMERALD), new ItemStack(Items.INK_SAC, 3)),
                    WitchTradeOffer.of(25, new ItemStack(Items.EMERALD, 2), new ItemStack(Items.PRISMARINE_SHARD, 4)),
                    WitchTradeOffer.amuletReward(50, new ItemStack(Items.EMERALD, 10))
            ),
            "Coven Mother Yaga", List.of(
                    WitchTradeOffer.of(0, new ItemStack(Items.EMERALD, 3), new ItemStack(Items.GLOWSTONE_DUST, 4))
            )
    );

    public static List<WitchTradeOffer> getFullPool(String witchName) {
        return POOLS.getOrDefault(witchName, List.of());
    }

    public static List<WitchTradeOffer> getAvailableOffers(String witchName, int playerReputation) {
        return getFullPool(witchName).stream()
                .filter(offer -> offer.requiredReputation() <= playerReputation)
                .sorted((a, b) -> Integer.compare(a.requiredReputation(), b.requiredReputation()))
                .toList();
    }
}