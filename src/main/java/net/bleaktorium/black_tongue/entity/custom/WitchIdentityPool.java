package net.bleaktorium.black_tongue.entity.custom;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.RandomAccess;

public class WitchIdentityPool {

    private static final List<WitchIdentity> POOL = List.of(
            new WitchIdentity(
                    "Yennefer",
                    ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/gui/dialog/coven_trade_yennefer.png"),
                    ResourceLocation.fromNamespaceAndPath("black_tongue", "geo/covenless_witch.geo.json"),
                    ResourceLocation.fromNamespaceAndPath("black_tongue", "textures/entity/yennefer.png")
            )
    );

    public static WitchIdentity rollRandom(net.minecraft.util.RandomSource random) {
        return POOL.get(random.nextInt(POOL.size()));
    }

    public static WitchIdentity getByName(String name) {
        return POOL.stream().filter(w -> w.name().equals(name)).findFirst().orElse(POOL.get(0));
    }
}