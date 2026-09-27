package net.bleaktorium.black_tongue.entity.custom;

import net.bleaktorium.black_tongue.coven.AmuletBinding;
import net.bleaktorium.black_tongue.coven.ModDataComponents;
import net.bleaktorium.black_tongue.coven.SummonedWitchType;
import net.bleaktorium.black_tongue.item.custom.CovenSummoningAmuletItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import java.util.Optional;

public class CovenlessWitchEntity extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private String identityName = null;

    // debug
    private int despawnTicksRemaining = -1;

    public void setIdentity(WitchIdentity identity) {
        this.identityName = identity.name();
    }

    public void startDespawnCountdown(int ticks) {
        this.despawnTicksRemaining = ticks;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && despawnTicksRemaining > 0) {
            despawnTicksRemaining--;
            if (despawnTicksRemaining == 0) {
                this.discard();
            }
        }
    }

    public CovenlessWitchEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    public WitchIdentity getIdentity() {
        if (identityName == null) {
            WitchIdentity rolled = WitchIdentityPool.rollRandom(this.getRandom());
            identityName = rolled.name();
            return rolled;
        }
        return WitchIdentityPool.getByName(identityName);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (identityName != null) tag.putString("WitchIdentity", identityName);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("WitchIdentity")) identityName = tag.getString("WitchIdentity");
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
        if (this.level().isClientSide) return InteractionResult.SUCCESS;

        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof CovenSummoningAmuletItem && held.get(ModDataComponents.AMULET_BINDING.get()) == null) {
            held.set(ModDataComponents.AMULET_BINDING.get(),
                    new AmuletBinding(SummonedWitchType.COVENLESS_WITCH, Optional.of(getIdentity().name())));
            player.displayClientMessage(Component.literal("The amulet now answers to " + getIdentity().name() + "."), true);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}