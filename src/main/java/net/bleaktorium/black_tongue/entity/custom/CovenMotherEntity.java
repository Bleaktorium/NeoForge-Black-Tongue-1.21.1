package net.bleaktorium.black_tongue.entity.custom;

import net.bleaktorium.black_tongue.coven.CovenPlayerData;
import net.bleaktorium.black_tongue.coven.ModAttachments;
import net.bleaktorium.black_tongue.dialog.DialogHandler;
import net.bleaktorium.black_tongue.dialog.DialogNode;
import net.bleaktorium.black_tongue.dialog.DialogOption;
import net.bleaktorium.black_tongue.dialog.DialogSessionManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.PathfinderMob;
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
                .add(Attributes.MOVEMENT_SPEED, 0.0);
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

    private int ticksUntilSwap = rollSwapDelay();
    private net.minecraft.core.BlockPos throneCenter = null;

    private static int rollSwapDelay() {
        return 45 * 20 + (int) (Math.random() * (75 * 20));
    }

    public void setThroneCenter(net.minecraft.core.BlockPos pos) {
        this.throneCenter = pos;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

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