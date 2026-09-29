package net.bleaktorium.black_tongue.entity.custom;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;

public class CovenHutEntity extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public enum HutState { WANDERING, MOVING_TO_SEED, SITTING, GETTING_UP, WAITING_FOR_YAGA }

    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(CovenHutEntity.class, EntityDataSerializers.INT);

    private int stateTicks = 0;
    private ItemEntity targetSeed = null;
    private CovenMotherEntity spawnedYaga = null;

    private static final int SIT_HOLD_TICKS = 60;
    private static final int GET_UP_TICKS = 20;

    public CovenHutEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.18);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, HutState.WANDERING.ordinal());
    }

    public HutState getState() {
        return HutState.values()[
                this.entityData.get(DATA_STATE)];
    }

    public void transitionTo(HutState newState) {
        this.entityData.set(DATA_STATE, newState.ordinal());
        this.stateTicks = 0;
    }

    public void setTargetSeed(ItemEntity seed) {
        this.targetSeed = seed; }
    public ItemEntity getTargetSeed() {
        return targetSeed; }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new GatedRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(0, new SeekSeedGoal(this));
    }

    private static class GatedRandomStrollGoal extends RandomStrollGoal {
        private final CovenHutEntity hut;

        GatedRandomStrollGoal(CovenHutEntity hut, double speedModifier) {
            super(hut, speedModifier);
            this.hut = hut;
        }

        @Override
        public boolean canUse() {
            return hut.getState() == CovenHutEntity.HutState.WANDERING && super.canUse();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        stateTicks++;

        switch (getState()) {
            case SITTING -> {
                if (stateTicks == 1) {
                    spawnYagaInFront();
                }
                if (stateTicks >= SIT_HOLD_TICKS) {
                    transitionTo(HutState.GETTING_UP);
                }
            }
            case GETTING_UP -> {
                if (stateTicks >= GET_UP_TICKS) {
                    transitionTo(HutState.WAITING_FOR_YAGA);
                }
            }
            case WAITING_FOR_YAGA -> {
                if (spawnedYaga == null || !spawnedYaga.isAlive()) {
                    spawnedYaga = null;
                    transitionTo(HutState.WANDERING);
                }
            }
            default -> { }
        }
    }

    private void spawnYagaInFront() {
        CovenMotherEntity yaga = net.bleaktorium.black_tongue.entity.ModEntities.COVEN_MOTHER.get().create(this.level());
        if (yaga == null) return;

        double angleRad = Math.toRadians(this.getYRot());
        double spawnX = this.getX() - Math.sin(angleRad) * 1.5;
        double spawnZ = this.getZ() + Math.cos(angleRad) * 1.5;

        yaga.setPos(spawnX, this.getY(), spawnZ);
        yaga.setPersistenceRequired();
        yaga.startIdleDespawnTimer(3 * 60 * 20);
        this.level().addFreshEntity(yaga);
        this.spawnedYaga = yaga;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animState) {
        boolean isPhysicallyMoving = this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6;

        switch (getState()) {
            case WANDERING, MOVING_TO_SEED -> {
                String clip = isPhysicallyMoving ? "walk" : "idle";
                animState.getController().setAnimation(RawAnimation.begin().then(clip, Animation.LoopType.LOOP));
            }
            case SITTING ->
                    animState.getController().setAnimation(RawAnimation.begin().then("sit_down", Animation.LoopType.HOLD_ON_LAST_FRAME));
            case GETTING_UP ->
                    animState.getController().setAnimation(RawAnimation.begin().then("get_up", Animation.LoopType.HOLD_ON_LAST_FRAME));
            case WAITING_FOR_YAGA ->
                    animState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private static class SeekSeedGoal extends Goal {
        private final CovenHutEntity hut;

        SeekSeedGoal(CovenHutEntity hut) {
            this.hut = hut;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (hut.getState() != CovenHutEntity.HutState.WANDERING) return false;
            return findNearestSeed() != null;
        }

        private ItemEntity findNearestSeed() {
            AABB area = hut.getBoundingBox().inflate(8.0);
            List<ItemEntity> seeds = hut.level().getEntitiesOfClass(ItemEntity.class, area,
                    e -> e.getItem().is(Items.WHEAT_SEEDS));
            return seeds.isEmpty() ? null : seeds.get(0);
        }

        @Override
        public void start() {
            ItemEntity seed = findNearestSeed();
            hut.setTargetSeed(seed);
            hut.transitionTo(CovenHutEntity.HutState.MOVING_TO_SEED);
        }

        @Override
        public boolean canContinueToUse() {
            ItemEntity seed = hut.getTargetSeed();
            return hut.getState() == CovenHutEntity.HutState.MOVING_TO_SEED
                    && seed != null && seed.isAlive();
        }

        @Override
        public void tick() {
            ItemEntity seed = hut.getTargetSeed();
            if (seed == null) return;

            hut.getNavigation().moveTo(seed.getX(), seed.getY(), seed.getZ(), 0.6);

            if (hut.distanceToSqr(seed) < 9.0) {
                seed.discard();
                hut.setTargetSeed(null);
                hut.transitionTo(CovenHutEntity.HutState.SITTING);
            }
        }

        @Override
        public void stop() {
            hut.getNavigation().stop();
        }
    }
}