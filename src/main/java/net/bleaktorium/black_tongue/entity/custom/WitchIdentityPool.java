package net.bleaktorium.black_tongue.entity.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.RandomAccess;

public class WitchIdentityPool {

    private static final List<WitchIdentity> POOL = List.of(
            new WitchIdentity(
                    "Yennefer",
                    ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/dialog/coven_trade_yennefer.png"),
                    ResourceLocation.fromNamespaceAndPath("black_tongue", "geo/covenless_witch.geo.json"),
                    ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/entity/yennefer.png")
            ),
            new WitchIdentity(
                    "Coven Mother Yaga",
                    ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/trade/coven_trade_yaga.png"),
                    ResourceLocation.fromNamespaceAndPath("black_tongue", "geo/coven_mother.geo.json"),
                    ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/entity/coven_mother.png"),
                    false
            )
    );

    public static WitchIdentity rollRandom(RandomSource random) {
        List<WitchIdentity> eligible = POOL.stream()
                .filter(WitchIdentity::isRandomlyAssignable)
                .toList();
        return eligible.get(random.nextInt(eligible.size()));
    }

    public static WitchIdentity getByName(String name) {
        return POOL.stream().filter(w -> w.name().equals(name)).findFirst().orElse(POOL.get(0));
    }
}