package net.bleaktorium.black_tongue.block.entity.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleTypes;

public final class CandleFlames {
    private CandleFlames() {}

    public static void smallFlame(double x, double y, double z, float scale) {
        Particle flame = Minecraft.getInstance().particleEngine
                .createParticle(ParticleTypes.SMALL_FLAME, x, y, z, 0, 0, 0);
        if (flame != null) flame.scale(scale);
    }
}