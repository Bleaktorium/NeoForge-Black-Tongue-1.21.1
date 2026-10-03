package net.bleaktorium.black_tongue.block.entity;

import net.bleaktorium.black_tongue.block.custom.EmbalmingTableBlock;
import net.bleaktorium.black_tongue.coven.ModDataComponents;
import net.bleaktorium.black_tongue.item.ModItems;
import net.bleaktorium.black_tongue.remains.RemainsData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EmbalmingTableBlockEntity extends BlockEntity implements GeoBlockEntity {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation POUR = RawAnimation.begin().thenPlay("pour_oil");

    public static final int POUR_TICKS = 126;
    public static final int LIQUID_GONE_TICK = 92;
    public static final int DRIP_START = 66;
    public static final int DRIP_END = 92;

    private long pourStart = -1;
    @Nullable
    private RemainsData.DecayTier pourOil = null;
    private long lastDripTick = -1;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private ItemStack remains = ItemStack.EMPTY;

    public EmbalmingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EMBALMING_TABLE_BE.get(), pos, state);
    }

    public boolean isEmpty() { return remains.isEmpty(); }
    public ItemStack getRemains() { return remains; }

    @Nullable
    public RemainsData getRemainsData() {
        return remains.get(ModDataComponents.REMAINS_DATA.get());
    }

    public void placeRemains(ItemStack stack) {
        remains = stack;
        sync();
    }

    public ItemStack takeRemains() {
        ItemStack taken = remains;
        remains = ItemStack.EMPTY;
        sync();
        return taken;
    }

    public void tickServer(ServerLevel level) {
        if (isPouring()) {
            if (pourAge() >= POUR_TICKS) finishPour(level);
            return;
        }
        if (remains.isEmpty() || level.getGameTime() % 20 != 0) return;
        RemainsData data = getRemainsData();
        if (data != null && data.tier(level) == RemainsData.DecayTier.DUST) {
            crumbleToDust(level);
        }
    }

    public Vec3 remainsCenter() {
        Direction side = EmbalmingTableBlock.sideDirection(getBlockState().getValue(EmbalmingTableBlock.FACING));
        return Vec3.atBottomCenterOf(worldPosition).add(side.getStepX() * 0.22, 0.8, side.getStepZ() * 0.22);
    }

    public void crumbleToDust(ServerLevel level) {
        remains = ItemStack.EMPTY;
        sync();
        Vec3 c = remainsCenter();
        level.sendParticles(ParticleTypes.WHITE_ASH, c.x, c.y, c.z, 40, 0.4, 0.1, 0.2, 0.02);
        level.sendParticles(ParticleTypes.POOF, c.x, c.y, c.z, 8, 0.3, 0.05, 0.15, 0.01);
        level.playSound(null, worldPosition, SoundEvents.SAND_BREAK, SoundSource.BLOCKS, 1.0F, 0.6F);
        ItemEntity dust = new ItemEntity(level, c.x, c.y + 0.1, c.z, new ItemStack(ModItems.ANCIENT_DUST.get()));
        dust.setDeltaMovement(0, 0.1, 0);
        level.addFreshEntity(dust);
    }

    private void sync() {
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Remains", remains.saveOptional(registries));
        tag.putLong("PourStart", pourStart);
        if (pourOil != null) tag.putString("PourOil", pourOil.name());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        remains = ItemStack.parseOptional(registries, tag.getCompound("Remains"));
        pourStart = tag.contains("PourStart") ? tag.getLong("PourStart") : -1;
        pourOil = tag.contains("PourOil") ? RemainsData.DecayTier.valueOf(tag.getString("PourOil")) : null;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> state.setAndContinue(IDLE))
                .triggerableAnim("pour_oil", POUR));
    }

    public boolean isPouring() {
        return pourStart >= 0;
    }

    public int pourAge() {
        if (level == null || !isPouring()) return -1;
        return (int) (level.getGameTime() - pourStart);
    }

    public boolean canPour() {
        RemainsData data = getRemainsData();
        return data != null && data.anointedTier().isEmpty() && !isPouring();
    }

    public void startPour(ServerLevel level, RemainsData.DecayTier oil) {
        pourStart = level.getGameTime();
        pourOil = oil;
        sync();
        triggerAnim("controller", "pour_oil");
    }

    public void dripOil(Vector3d at) {
        if (level == null || level.getGameTime() == lastDripTick) return;
        lastDripTick = level.getGameTime();
        level.addParticle(ParticleTypes.FALLING_HONEY, at.x, at.y, at.z, 0, 0, 0);
    }

    private void finishPour(ServerLevel level) {
        RemainsData.DecayTier oil = pourOil;
        pourStart = -1;
        pourOil = null;

        RemainsData data = getRemainsData();
        if (data == null || oil == null) { sync(); return; }

        Vec3 c = remainsCenter();
        if (oil == data.tier(level)) {
            remains.set(ModDataComponents.REMAINS_DATA.get(), data.withAnointedTier(oil));
            level.sendParticles(ParticleTypes.END_ROD, c.x, c.y + 0.2, c.z, 15, 0.3, 0.2, 0.3, 0.01);
            level.playSound(null, worldPosition, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0F, 0.8F);
            sync();
        } else if (data.displeasure() == 0) {
            // First wrong oil
            remains.set(ModDataComponents.REMAINS_DATA.get(), data.withDispleasure(1));
            level.sendParticles(ParticleTypes.ANGRY_VILLAGER, c.x, c.y + 0.4, c.z, 6, 0.3, 0.1, 0.3, 0.0);
            level.playSound(null, worldPosition, SoundEvents.SKELETON_HURT, SoundSource.BLOCKS, 0.8F, 0.5F);
            sync();
        } else {
            // Second wrong oil
            level.sendParticles(ParticleTypes.SOUL, c.x, c.y + 0.5, c.z, 40, 0.25, 0.4, 0.25, 0.04);
            level.playSound(null, worldPosition, SoundEvents.PHANTOM_DEATH, SoundSource.BLOCKS, 1.0F, 1.0F);
            crumbleToDust(level);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache; }
}