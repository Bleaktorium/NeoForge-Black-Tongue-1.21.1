package net.bleaktorium.black_tongue.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CovenMotherCatEntity extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int ticksUntilSwap = 45 * 20 + (int) (Math.random() * (75 * 20));
    private BlockPos throneCenter = null;

    public CovenMotherCatEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.7));
    }

    public void setThroneCenter(BlockPos pos) {
        this.throneCenter = pos;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        boolean playerNearby = !this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(4.0)).isEmpty();
        if (playerNearby) return;

        ticksUntilSwap--;
        if (ticksUntilSwap <= 0) {
            transformToHumanoid(null);
        }
    }

    private void transformToHumanoid(ServerPlayer interactingPlayer) {
        CovenMotherEntity yaga = net.bleaktorium.black_tongue.entity.ModEntities.COVEN_MOTHER.get().create(this.level());
        if (yaga == null) return;

        yaga.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0);
        yaga.setThroneCenter(throneCenter);
        yaga.setPersistenceRequired();
        this.level().addFreshEntity(yaga);
        this.discard();

        if (interactingPlayer != null) {
            CovenDialogOpener.open(interactingPlayer);
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            transformToHumanoid(serverPlayer);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        state.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}