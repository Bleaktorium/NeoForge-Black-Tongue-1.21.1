package net.bleaktorium.black_tongue.entity.custom;

import net.bleaktorium.black_tongue.block.ModBlocks;
import net.bleaktorium.black_tongue.block.custom.PagankaRootCropBlock;
import net.bleaktorium.black_tongue.block.entity.PagankaRootCropBlockEntity;
import net.bleaktorium.black_tongue.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;

public class PagankaRootEntity extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation MOVING = RawAnimation.begin().thenLoop("moving");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("death");
    private static final RawAnimation MISCHIEF = RawAnimation.begin().thenPlay("mischief");

    private static final int DESPAWN_CHECK_INTERVAL = 10 * 20;
    private static final double DESPAWN_CHANCE = 0.02; // PLACEHOLDER

    private BlockPos gardenPos = null;
    private int ticksAlive = 0;

    public PagankaRootEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    public void setGardenPos(BlockPos pos) { this.gardenPos = pos; }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(0, new EatNearbyCropGoal(this));
    }

    private static final EntityDataAccessor<Integer> MISCHIEF_TICKS =
            SynchedEntityData.defineId(PagankaRootEntity.class, EntityDataSerializers.INT);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MISCHIEF_TICKS, 0);
    }

    private int getMischiefTicks() { return this.entityData.get(MISCHIEF_TICKS); }
    private void setMischiefTicks(int ticks) { this.entityData.set(MISCHIEF_TICKS, ticks); }


    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        ticksAlive++;
        if (getMischiefTicks() > 0) setMischiefTicks(getMischiefTicks() - 1);

        if (ticksAlive > 60 && level().isDay()) {
            returnToGarden();
            return;
        }

        if (this.tickCount % DESPAWN_CHECK_INTERVAL == 0 && random.nextDouble() < DESPAWN_CHANCE) {
            this.discard();
        }
    }

    private void returnToGarden() {
        if (level() instanceof ServerLevel serverLevel && gardenPos != null) {
            if (serverLevel.getBlockState(gardenPos).isAir()) {
                BlockState ripe = ModBlocks.PAGANKA_ROOT.get().defaultBlockState()
                        .setValue(CropBlock.AGE, 5)
                        .setValue(PagankaRootCropBlock.WAITED_ONE_NIGHT, true);
                serverLevel.setBlock(gardenPos, ripe, 3);
            }
        }
        this.discard();
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        if (recentlyHit) {
            this.spawnAtLocation(new ItemStack(ModItems.PAGANKA_ROOT.get()));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (gardenPos != null) tag.putLong("GardenPos", gardenPos.asLong());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("GardenPos")) gardenPos = BlockPos.of(tag.getLong("GardenPos"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        if (this.isDeadOrDying()) {
            return state.setAndContinue(DEATH);
        }
        if (getMischiefTicks() > 0) {
            return state.setAndContinue(MISCHIEF);
        }
        boolean moving = this.walkAnimation.speed() > 0.01F;
        return state.setAndContinue(moving ? MOVING : IDLE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private static class EatNearbyCropGoal extends Goal {
        private static final int CHECK_INTERVAL = 100;
        private static final double EAT_CHANCE_PER_CHECK = 0.15;
        private static final int GIVE_UP_TICKS = 200;

        private final PagankaRootEntity mob;
        private BlockPos target = null;
        private int timeLeft = 0;

        EatNearbyCropGoal(PagankaRootEntity mob) {
            this.mob = mob;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (mob.tickCount % CHECK_INTERVAL != 0) return false;
            if (!mob.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) return false;
            if (mob.random.nextDouble() > EAT_CHANCE_PER_CHECK) return false;
            target = findNearbyCrop();
            return target != null;
        }

        private BlockPos findNearbyCrop() {
            BlockPos center = mob.blockPosition();
            BlockPos best = null;
            double bestDist = Double.MAX_VALUE;
            for (BlockPos pos : BlockPos.betweenClosed(center.offset(-6, -1, -6), center.offset(6, 1, 6))) {
                Block block = mob.level().getBlockState(pos).getBlock();
                if (!(block instanceof CropBlock)) continue;
                if (block instanceof PagankaRootCropBlock) continue;
                double dist = pos.distSqr(center);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = pos.immutable();
                }
            }
            return best;
        }

        @Override
        public void start() {
            timeLeft = GIVE_UP_TICKS;
            Path path = mob.getNavigation().createPath(target, 0);
            mob.getNavigation().moveTo(path, 0.6);
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && timeLeft > 0
                    && mob.level().getBlockState(target).getBlock() instanceof CropBlock;
        }

        @Override
        public void tick() {
            timeLeft--;
            BlockPos feet = mob.blockPosition();
            boolean standingOnIt = feet.getX() == target.getX()
                    && feet.getZ() == target.getZ()
                    && Math.abs(feet.getY() - target.getY()) <= 1;

            if (standingOnIt) {
                mob.level().removeBlock(target, false);
                mob.setMischiefTicks(20);
                target = null;
            } else if (mob.getNavigation().isDone()) {
                // path ended just short: shuffle the last bit to the center
                mob.getMoveControl().setWantedPosition(
                        target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 0.6);
            }
        }

        @Override
        public void stop() {
            target = null;
            mob.getNavigation().stop();
        }
    }
}