package net.bleaktorium.black_tongue.entity.custom;

import net.bleaktorium.black_tongue.coven.CovenPlayerData;
import net.bleaktorium.black_tongue.coven.ModAttachments;
import net.bleaktorium.black_tongue.dialog.DialogHandler;
import net.bleaktorium.black_tongue.dialog.DialogNode;
import net.bleaktorium.black_tongue.dialog.DialogOption;
import net.bleaktorium.black_tongue.dialog.DialogSessionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import java.util.List;

public class CovenMotherEntity extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CovenMotherEntity(EntityType<? extends PathfinderMob> type, Level level) {

        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2);
    }

    private int guestTicksRemaining = -1;
    public void startGuestCountdown(int ticks) {
        this.guestTicksRemaining = ticks;
    }

    private int idleDespawnTicksRemaining = -1;

    public void startIdleDespawnTimer(int ticks) {

        this.idleDespawnTicksRemaining = ticks;
    }

    private void resetIdleDespawnTimer() {
        if (idleDespawnTicksRemaining >= 0) {
            idleDespawnTicksRemaining = 3 * 60 * 20;
        }
    }

    private static class PausingStrollGoal extends RandomStrollGoal {
        private final CovenMotherEntity yaga;

        PausingStrollGoal(CovenMotherEntity yaga, double speedModifier) {
            super(yaga, speedModifier);
            this.yaga = yaga;
        }

        private boolean playerNearby() {
            return !yaga.level().getEntitiesOfClass(Player.class, yaga.getBoundingBox().inflate(5.0)).isEmpty();
        }

        @Override
        public boolean canUse() {
            if (yaga.throneCenter == null) return false;

            if (playerNearby()) return false;
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {

            return !playerNearby() && super.canContinueToUse();
        }
    }

    private int ticksUntilSwap = rollSwapDelay();
    private net.minecraft.core.BlockPos throneCenter = null;

    private static int rollSwapDelay() {

        return 45 * 20 + (int) (Math.random() * (75 * 20));
    }

    public static final int WANDER_RADIUS = 5;

    public void setThroneCenter(net.minecraft.core.BlockPos pos) {
        this.throneCenter = pos;
        this.restrictTo(pos, WANDER_RADIUS);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MoveTowardsRestrictionGoal(this, 0.6)); 
        this.goalSelector.addGoal(1, new PausingStrollGoal(this, 0.6));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (throneCenter != null) tag.putLong("ThroneCenter", throneCenter.asLong());
        tag.putInt("IdleDespawn", idleDespawnTicksRemaining);
        tag.putInt("GuestDespawn", guestTicksRemaining);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ThroneCenter")) {
            throneCenter = BlockPos.of(tag.getLong("ThroneCenter"));
            this.restrictTo(throneCenter, WANDER_RADIUS);
        }
        idleDespawnTicksRemaining = tag.contains("IdleDespawn") ? tag.getInt("IdleDespawn") : -1;
        guestTicksRemaining = tag.contains("GuestDespawn") ? tag.getInt("GuestDespawn") : -1;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        if (guestTicksRemaining > 0) {
            guestTicksRemaining--;
            if (guestTicksRemaining == 0) {
                this.discard();
                return;
            }
        }

        if (!this.level().isClientSide && idleDespawnTicksRemaining > 0) {
            idleDespawnTicksRemaining--;
            if (idleDespawnTicksRemaining == 0) {
                this.discard();
                return;
            }
        }

        if (throneCenter == null) return;
        boolean playerNearby = !this.level().getEntitiesOfClass(net.minecraft.world.entity.player.Player.class,
                this.getBoundingBox().inflate(4.0)).isEmpty();
        if (playerNearby) return;

        ticksUntilSwap--;
        if (ticksUntilSwap <= 0) {
            transformToCat();
        }
    }

    private void transformToCat() {
        CovenMotherCatEntity cat = net.bleaktorium.black_tongue.entity.ModEntities.COVEN_MOTHER_CAT.get().create(this.level());
        if (cat == null) return;

        cat.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0);
        cat.setThroneCenter(throneCenter);
        cat.setPersistenceRequired();
        this.level().addFreshEntity(cat);
        this.discard();
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

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        resetIdleDespawnTimer();
        if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            CovenPlayerData data = serverPlayer.getData(ModAttachments.COVEN_DATA.get());

            DialogNode root = switch (data.state()) {
                case NEVER_ASKED -> YagaDialogTrees.firstMeeting();
                case TASK_DECLINED -> YagaDialogTrees.followUpB();
                case TASK_ACCEPTED -> YagaDialogTrees.followUpA();
                case POTION_DELIVERED -> YagaDialogTrees.followUpD();
                case GRIMOIRE_RECEIVED -> YagaDialogTrees.followUpC();
            };

            DialogSessionManager.startSession(serverPlayer, root, "Coven Mother Yaga", "witchcraft");
            DialogHandler.sendNode(serverPlayer, root);
        }
        return InteractionResult.SUCCESS;
    }


}