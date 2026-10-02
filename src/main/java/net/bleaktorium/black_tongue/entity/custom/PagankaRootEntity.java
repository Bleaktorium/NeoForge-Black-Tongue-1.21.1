package net.bleaktorium.black_tongue.entity.custom;

import net.bleaktorium.black_tongue.block.ModBlocks;
import net.bleaktorium.black_tongue.block.custom.PagankaRootCropBlock;
import net.bleaktorium.black_tongue.block.entity.PagankaRootCropBlockEntity;
import net.bleaktorium.black_tongue.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
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

    private static final int DESPAWN_CHECK_INTERVAL = 10 * 20;
    private static final double DESPAWN_CHANCE = 0.02; // PLACEHOLDER

    private BlockPos gardenPos = null;
    private int mischiefTicksRemaining = 0;
    private int ticksAlive = 0;

    public PagankaRootEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 3.0) // same as a rabbit
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    public void setGardenPos(BlockPos pos) { this.gardenPos = pos; }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(0, new EatNearbyCropGoal(this));
    }


    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        ticksAlive++;
        if (mischiefTicksRemaining > 0) mischiefTicksRemaining--;

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
                        .setValue(CropBlock.AGE, 5);
                serverLevel.setBlock(gardenPos, ripe, 3);

                if (serverLevel.getBlockEntity(gardenPos) instanceof PagankaRootCropBlockEntity be) {
                    be.setRipenedOnDay(serverLevel.getDayTime() / 24000L);
                }
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
            state.setAndContinue(RawAnimation.begin().then("death", Animation.LoopType.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        }
        if (mischiefTicksRemaining > 0) {
            state.setAndContinue(RawAnimation.begin().then("mischief", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        boolean moving = this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6;
        state.setAndContinue(RawAnimation.begin().then(moving ? "walking" : "idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private static class EatNearbyCropGoal extends Goal {
        private static final int CHECK_INTERVAL = 100; // ~5s
        private static final double EAT_CHANCE_PER_CHECK = 0.15;

        private final PagankaRootEntity mob;
        private BlockPos target = null;

        EatNearbyCropGoal(PagankaRootEntity mob) {
            this.mob = mob;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (mob.tickCount % CHECK_INTERVAL != 0) return false;
            if (mob.random.nextDouble() > EAT_CHANCE_PER_CHECK) return false;
            target = findNearbyCrop();
            return target != null;
        }

        private BlockPos findNearbyCrop() {
            AABB area = mob.getBoundingBox().inflate(6.0);
            BlockPos center = mob.blockPosition();
            for (BlockPos pos : BlockPos.betweenClosed(
                    new BlockPos((int) area.minX, (int) area.minY, (int) area.minZ),
                    new BlockPos((int) area.maxX, (int) area.maxY, (int) area.maxZ))) {
                if (mob.level().getBlockState(pos).getBlock() instanceof CropBlock) {
                    return pos.immutable();
                }
            }
            return null;
        }

        @Override
        public void start() {
            if (target != null) mob.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 0.6);
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && mob.level().getBlockState(target).getBlock() instanceof CropBlock;
        }

        @Override
        public void tick() {
            if (target == null) return;
            if (mob.distanceToSqr(target.getX() + 0.5, target.getY(), target.getZ() + 0.5) < 2.5) {
                mob.level().removeBlock(target, false);
                mob.mischiefTicksRemaining = 15;
                target = null;
            }
        }
    }
}