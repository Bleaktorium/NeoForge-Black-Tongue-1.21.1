package net.bleaktorium.black_tongue.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.Level;

public class CovenBiomeHelper {
    public static boolean isSwamp(Level level, BlockPos pos) {
        var biomeHolder = level.getBiome(pos);
        return biomeHolder.is(Biomes.SWAMP) || biomeHolder.is(Biomes.MANGROVE_SWAMP);
    }
}