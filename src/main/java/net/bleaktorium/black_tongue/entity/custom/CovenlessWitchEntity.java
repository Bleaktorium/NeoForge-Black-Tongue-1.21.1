package net.bleaktorium.black_tongue.entity.custom;

import net.bleaktorium.black_tongue.coven.*;
import net.bleaktorium.black_tongue.item.ModItems;
import net.bleaktorium.black_tongue.item.custom.CovenSummoningAmuletItem;
import net.bleaktorium.black_tongue.remains.RemainsData;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import java.util.EnumSet;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CovenlessWitchEntity extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<String> IDENTITY =
            SynchedEntityData.defineId(CovenlessWitchEntity.class, EntityDataSerializers.STRING);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IDENTITY, "");
    }

    private int despawnTicksRemaining = -1; //debug
    private static final int HOME_RADIUS = 10;

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new CastSpellGoal(this));
        this.goalSelector.addGoal(2, new MoveTowardsRestrictionGoal(this, 0.6));
        this.goalSelector.addGoal(3, new WanderGoal(this, 0.6));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, CovenlessWitchEntity.class));
    }

    private static class CastSpellGoal extends Goal {
        private final CovenlessWitchEntity witch;
        private int cooldown = 20;

        CastSpellGoal(CovenlessWitchEntity witch) {
            this.witch = witch;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = witch.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void stop() {
            witch.getNavigation().stop();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = witch.getTarget();
            if (target == null) return;
            WitchSpell spell = WitchSpell.of(witch.getIdentity().name());

            witch.getLookControl().setLookAt(target, 30.0F, 30.0F);
            boolean inRange = witch.distanceTo(target) <= spell.range() && witch.getSensing().hasLineOfSight(target);
            if (inRange) witch.getNavigation().stop();
            else witch.getNavigation().moveTo(target, 0.7);

            if (cooldown > 0) {
                cooldown--;
            } else if (inRange) {
                spell.cast(witch, target);
                cooldown = spell.cooldown();
            }
        }
    }

    private static class WanderGoal extends WaterAvoidingRandomStrollGoal {
        private final CovenlessWitchEntity witch;

        WanderGoal(CovenlessWitchEntity witch, double speed) {
            super(witch, speed);
            this.witch = witch;
        }

        @Override
        public boolean canUse() {
            return witch.despawnTicksRemaining < 0 && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return witch.despawnTicksRemaining < 0 && super.canContinueToUse();
        }
    }


    public void setIdentity(WitchIdentity identity) {
        this.entityData.set(IDENTITY, identity.name());
    }

    public void startDespawnCountdown(int ticks) {
        this.despawnTicksRemaining = ticks;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.entityData.get(IDENTITY).isEmpty()) {
            getIdentity();
        }
        if (!this.level().isClientSide && !this.hasRestriction() && despawnTicksRemaining < 0) {
            this.restrictTo(this.blockPosition(), HOME_RADIUS);
        }
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
        String name = this.entityData.get(IDENTITY);
        if (name.isEmpty() && !this.level().isClientSide) {
            WitchIdentity rolled = WitchIdentityPool.rollRandom(this.getRandom());
            this.entityData.set(IDENTITY, rolled.name());
            return rolled;
        }
        return WitchIdentityPool.getByName(name);
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
        String name = this.entityData.get(IDENTITY);
        if (!name.isEmpty()) tag.putString("WitchIdentity", name);
        if (soulId != null) tag.putUUID("SoulId", soulId);
        if (this.hasRestriction()) tag.putLong("Home", this.getRestrictCenter().asLong());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        despawnTicksRemaining = tag.contains("DespawnTicks") ? tag.getInt("DespawnTicks") : -1;
        super.readAdditionalSaveData(tag);
        if (tag.contains("WitchIdentity")) this.entityData.set(IDENTITY, tag.getString("WitchIdentity"));
        if (tag.hasUUID("SoulId")) soulId = tag.getUUID("SoulId");
        if (tag.contains("Home")) this.restrictTo(BlockPos.of(tag.getLong("Home")), HOME_RADIUS);
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide && !this.dead && this.level() instanceof ServerLevel serverLevel) {
            FallenWitchesData.get(serverLevel.getServer()).markFallen(getSoulId());
            String name = getIdentity().name();
            List<Coven> leftCovens = CovenService.removeDeadWitch(serverLevel.getServer(), getSoulId().toString());

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
        controllers.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walking");

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        state.getController().setAnimation(state.isMoving() ? WALK : IDLE);
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