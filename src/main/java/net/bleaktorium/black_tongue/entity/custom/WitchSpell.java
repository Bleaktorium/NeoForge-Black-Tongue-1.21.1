package net.bleaktorium.black_tongue.entity.custom;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public enum WitchSpell {
    HEX_LASH,   // Yennefer
    ROT_CLOUD,  // Doedre
    UNDERTOW;   // Marina

    // Which witch knows which spell. Anyone not listed gets Hex Lash.
    public static WitchSpell of(String witchName) {
        return switch (witchName) {
            case "Doedre" -> ROT_CLOUD;
            case "Marina" -> UNDERTOW;
            default -> HEX_LASH;
        };
    }

    // Ticks between casts (20 ticks = 1 second).
    public int cooldown() {
        return switch (this) {
            case HEX_LASH -> 60;
            case ROT_CLOUD -> 140;
            case UNDERTOW -> 100;
        };
    }

    // How close the target must be before she casts.
    public double range() {
        return this == UNDERTOW ? 5.0 : 14.0;
    }

    public void cast(CovenlessWitchEntity witch, LivingEntity target) {
        if (!(witch.level() instanceof ServerLevel level)) return;
        switch (this) {
            case HEX_LASH -> hexLash(level, witch, target);
            case ROT_CLOUD -> rotCloud(level, witch, target);
            case UNDERTOW -> undertow(level, witch);
        }
    }

    // A beam of witch sparkles from her eyes to the target, then a purple burst.
    private static void hexLash(ServerLevel level, CovenlessWitchEntity witch, LivingEntity target) {
        Vec3 from = witch.getEyePosition();
        Vec3 to = target.getBoundingBox().getCenter();
        Vec3 line = to.subtract(from);
        int points = (int) (line.length() * 4);
        for (int i = 0; i <= points; i++) {
            Vec3 p = from.add(line.scale(i / (double) points));
            level.sendParticles(ParticleTypes.WITCH, p.x, p.y, p.z, 1, 0, 0, 0, 0);
        }
        level.sendParticles(new DustParticleOptions(new Vector3f(0.45F, 0.1F, 0.6F), 1.5F),
                to.x, to.y, to.z, 20, 0.3, 0.4, 0.3, 0);

        target.hurt(witch.damageSources().indirectMagic(witch, witch), 4.0F);
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0), witch);
        level.playSound(null, witch.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1.0F, 1.2F);
    }

    // A lingering cloud at the target's feet, like a lingering potion that shrinks away.
    private static void rotCloud(ServerLevel level, CovenlessWitchEntity witch, LivingEntity target) {
        AreaEffectCloud cloud = new AreaEffectCloud(level, target.getX(), target.getY(), target.getZ());
        cloud.setOwner(witch);
        cloud.setRadius(2.5F);
        cloud.setRadiusOnUse(-0.3F);
        cloud.setWaitTime(10);
        cloud.setDuration(100);
        cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
        cloud.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0));
        cloud.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0));
        level.addFreshEntity(cloud);
        level.playSound(null, witch.blockPosition(), SoundEvents.EVOKER_PREPARE_ATTACK, SoundSource.NEUTRAL, 1.0F, 0.8F);
    }

    // A ring of splashing water that shoves everyone nearby away from her.
    private static void undertow(ServerLevel level, CovenlessWitchEntity witch) {
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, witch.getBoundingBox().inflate(5.0),
                e -> e.isAlive() && !(e instanceof CovenlessWitchEntity))) {
            Vec3 push = e.position().subtract(witch.position()).normalize().scale(1.4);
            e.push(push.x, 0.5, push.z);
            e.hurtMarked = true; // tells the game to send the new speed to players
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1), witch);
        }
        for (int i = 0; i < 40; i++) {
            double angle = i * Math.PI * 2 / 40;
            double x = witch.getX() + Math.cos(angle) * 2.5;
            double z = witch.getZ() + Math.sin(angle) * 2.5;
            level.sendParticles(ParticleTypes.SPLASH, x, witch.getY() + 0.2, z, 3, 0.1, 0.1, 0.1, 0);
            level.sendParticles(ParticleTypes.BUBBLE_POP, x, witch.getY() + 0.5, z, 1, 0.1, 0.2, 0.1, 0);
        }
        level.playSound(null, witch.blockPosition(), SoundEvents.PLAYER_SPLASH_HIGH_SPEED, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }
}