package net.bleaktorium.black_tongue.entity.custom;

import net.bleaktorium.black_tongue.coven.*;
import net.bleaktorium.black_tongue.item.ModItems;
import net.bleaktorium.black_tongue.item.custom.CovenSummoningAmuletItem;
import net.bleaktorium.black_tongue.remains.RemainsData;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.bleaktorium.black_tongue.coven.FallenWitchesData;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Nullable
    private UUID soulId = null;

    public UUID getSoulId() {
        if (soulId == null) soulId = UUID.randomUUID();
        return soulId;
    }

    public void setSoulId(UUID soulId) {
        this.soulId = soulId;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("DespawnTicks", despawnTicksRemaining);
        super.addAdditionalSaveData(tag);
        if (identityName != null) tag.putString("WitchIdentity", identityName);
        if (soulId != null) tag.putUUID("SoulId", soulId);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        despawnTicksRemaining = tag.contains("DespawnTicks") ? tag.getInt("DespawnTicks") : -1;
        super.readAdditionalSaveData(tag);
        if (tag.contains("WitchIdentity")) identityName = tag.getString("WitchIdentity");
        if (tag.hasUUID("SoulId")) soulId = tag.getUUID("SoulId");
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide && !this.dead && this.level() instanceof ServerLevel serverLevel) {
            FallenWitchesData.get(serverLevel.getServer()).markFallen(getSoulId());
            String name = getIdentity().name();
            List<Coven> leftCovens = CovenService.removeDeadWitch(serverLevel.getServer(), name);

            if (!leftCovens.isEmpty()) {
                ItemStack remains = new ItemStack(ModItems.WITCH_REMAINS.get());
                remains.set(ModDataComponents.REMAINS_DATA.get(),
                        RemainsData.collectedNow(RemainsData.Origin.WITCH, name, serverLevel));
                this.spawnAtLocation(remains);

                for (Coven coven : leftCovens) {
                    ServerPlayer founder = serverLevel.getServer().getPlayerList().getPlayer(coven.founderId());
                    if (founder != null) {
                        founder.sendSystemMessage(Component.literal(name + " has died. Her seat in "
                                + coven.name() + " stands empty.").withStyle(ChatFormatting.DARK_PURPLE));
                    }
                }
            }
        }
        super.die(source);
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
                    new AmuletBinding(SummonedWitchType.COVENLESS_WITCH,
                            Optional.of(getIdentity().name()),
                            Optional.of(getSoulId())));
            player.displayClientMessage(Component.literal("The amulet now answers to " + getIdentity().name() + "."), true);
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            String name = getIdentity().name();
            UUID soul = getSoulId();
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, inv, p) -> new WitchTradeMenu(containerId, inv, name, soul, (ServerPlayer) p),
                    Component.literal(name)
            ), buf -> buf.writeUtf(name));
        }

        return InteractionResult.SUCCESS;
    }
}