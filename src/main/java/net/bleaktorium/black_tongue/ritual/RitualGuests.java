package net.bleaktorium.black_tongue.ritual;

import net.bleaktorium.black_tongue.coven.AmuletBinding;
import net.bleaktorium.black_tongue.entity.ModEntities;
import net.bleaktorium.black_tongue.entity.custom.CovenMotherCatEntity;
import net.bleaktorium.black_tongue.entity.custom.CovenMotherEntity;
import net.bleaktorium.black_tongue.entity.custom.CovenlessWitchEntity;
import net.bleaktorium.black_tongue.entity.custom.WitchIdentityPool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RitualGuests {

    @Nullable
    public static Entity summon(ServerLevel level, BlockPos pos, BlockPos tablePos, AmuletBinding binding, int safetyTicks) {
        Entity guest = switch (binding.type()) {
            case COVEN_MOTHER -> createYaga(level, pos, safetyTicks);
            case COVENLESS_WITCH -> createCovenless(level, binding, safetyTicks);
        };
        if (guest == null) return null;

        double x = pos.getX() + 0.5, y = pos.getY(), z = pos.getZ() + 0.5;

        float yaw = (float) (Math.atan2(tablePos.getZ() + 0.5 - z, tablePos.getX() + 0.5 - x) * (180.0 / Math.PI)) - 90.0f;
        guest.moveTo(x, y, z, yaw, 0.0f);
        if (guest instanceof LivingEntity living) {
            living.setYBodyRot(yaw);
            living.setYHeadRot(yaw);
        }
        level.addFreshEntity(guest);
        level.sendParticles(ParticleTypes.WITCH, x, y + 1.0, z, 15, 0.3, 0.6, 0.3, 0.05);
        return guest;
    }

    public static void startLinger(ServerLevel level, UUID id, int ticks) {
        Entity e = level.getEntity(id);
        if (e instanceof CovenlessWitchEntity witch) {
            witch.startDespawnCountdown(ticks);
        } else if (e instanceof CovenMotherEntity yaga) {
            yaga.startGuestCountdown(ticks);
        }
    }

    private static Entity createYaga(ServerLevel level, BlockPos near, int safetyTicks) {
        AABB area = new AABB(near).inflate(128);
        level.getEntitiesOfClass(CovenMotherEntity.class, area).forEach(Entity::discard);
        level.getEntitiesOfClass(CovenMotherCatEntity.class, area).forEach(Entity::discard);

        CovenMotherEntity yaga = ModEntities.COVEN_MOTHER.get().create(level);
        if (yaga == null) return null;
        yaga.setPersistenceRequired();
        yaga.startGuestCountdown(safetyTicks);
        return yaga;
    }

    private static Entity createCovenless(ServerLevel level, AmuletBinding binding, int safetyTicks) {
        String name = binding.witchName().orElse(null);
        if (name == null) return null;

        CovenlessWitchEntity witch = ModEntities.COVENLESS_WITCH.get().create(level);
        if (witch == null) return null;
        witch.setIdentity(WitchIdentityPool.getByName(name));
        witch.setPersistenceRequired();
        witch.startDespawnCountdown(safetyTicks);
        return witch;
    }
}