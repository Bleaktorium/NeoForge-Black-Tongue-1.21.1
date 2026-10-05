package net.bleaktorium.black_tongue.entity.custom;

import net.bleaktorium.black_tongue.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BansheeEntity extends PathfinderMob implements GeoEntity {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation MOVING = RawAnimation.begin().thenLoop("moving");

    public static final int COMB_COOLDOWN = 5 * 60 * 20; // 5 minutes

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private long combedAt = -1;

    public BansheeEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    public boolean canBeCombed() {
        return combedAt < 0 || level().getGameTime() - combedAt >= COMB_COOLDOWN;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(ModItems.GOLDEN_COMB.get())) return super.mobInteract(player, hand);

        if (level() instanceof ServerLevel serverLevel) {
            if (!canBeCombed()) return InteractionResult.PASS;
            combedAt = serverLevel.getGameTime();
            spawnAtLocation(new ItemStack(ModItems.BANSHEES_WISP.get(), 1 + random.nextInt(2)), 1.0F);
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            serverLevel.playSound(null, this, SoundEvents.SHEEP_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.4F);
            serverLevel.sendParticles(ParticleTypes.SOUL, getX(), getY() + 1.6, getZ(), 6, 0.3, 0.3, 0.3, 0.01);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putLong("CombedAt", combedAt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        combedAt = tag.contains("CombedAt") ? tag.getLong("CombedAt") : -1;
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.GHAST_AMBIENT; }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.GHAST_HURT; }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GHAST_DEATH; }

    @Override
    protected float getSoundVolume() {
        return 0.4F; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 4,
                state -> state.setAndContinue(walkAnimation.speed() > 0.01F ? MOVING : IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}