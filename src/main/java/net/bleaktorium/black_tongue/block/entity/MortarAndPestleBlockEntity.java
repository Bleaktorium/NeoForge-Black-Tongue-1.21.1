package net.bleaktorium.black_tongue.block.entity;

import net.bleaktorium.black_tongue.farming.HerbGrindingRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

public class MortarAndPestleBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation MUSHING = RawAnimation.begin().thenPlay("mushing");
    private static final int GRIND_TOTAL_TICKS = 115;
    private static final int GRACE_TICKS = 3;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private ItemStack storedItem = ItemStack.EMPTY;
    private int grindTicksAccumulated = 0;
    private boolean grinding = false;
    private double animClock = 0;
    private double lastRealTick = -1;

    private boolean isGrindPaused() {
        return hasItem() && grindTicksAccumulated > 0 && !grinding;
    }

    @Override
    public double getTick(Object blockEntity) {
        double now = RenderUtil.getCurrentTick();
        if (lastRealTick >= 0 && !isGrindPaused()) {
            animClock += now - lastRealTick;
        }
        lastRealTick = now;
        return animClock;
    }

    public MortarAndPestleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MORTAR_AND_PESTLE_BE.get(), pos, state);
    }

    public boolean hasItem() {
        return !storedItem.isEmpty(); }
    public ItemStack getStoredItem() {
        return storedItem; }

    public void setStoredItem(ItemStack stack) {
        this.storedItem = stack;
        this.grindTicksAccumulated = 0;
        setChanged();
        syncToClients();
    }

    private boolean signalReceivedThisPeriod = false;
    private int ticksWithoutSignal = 0;

    public void receiveGrindSignal(ServerLevel level) {
        signalReceivedThisPeriod = true;
        ticksWithoutSignal = 0;
        grinding = true;
        advanceGrind(level);
        syncToClients();
    }

    private void advanceGrind(ServerLevel level) {
        if (!hasItem()) return;
        grindTicksAccumulated++;
        if (grindTicksAccumulated >= GRIND_TOTAL_TICKS) {
            completeGrind(level);
        }
    }

    public void tickGrindTimeout(ServerLevel level) {
        if (!signalReceivedThisPeriod) {
            ticksWithoutSignal++;
            if (ticksWithoutSignal > GRACE_TICKS && grinding) {
                grinding = false;
                syncToClients();
            }
        }
        signalReceivedThisPeriod = false;
    }

    private void completeGrind(ServerLevel level) {
        ItemStack result = HerbGrindingRecipes.getResult(storedItem.getItem());
        if (result != null) {
            ItemEntity drop = new ItemEntity(level, worldPosition.getX() + 0.5,
                    worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, result);
            drop.setDeltaMovement(0, 0.15, 0);
            level.addFreshEntity(drop);
            level.sendParticles(ParticleTypes.POOF,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5,
                    8, 0.15, 0.1, 0.15, 0.02);
        }
        setStoredItem(ItemStack.EMPTY);
        grindTicksAccumulated = 0;
        grinding = false;
        syncToClients();
    }

    private void syncToClients() {
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        if (!hasItem() || grindTicksAccumulated == 0) {
            return state.setAndContinue(IDLE);
        }
        return state.setAndContinue(MUSHING);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!storedItem.isEmpty()) {
            tag.put("StoredItem", storedItem.save(registries));
        }
        tag.putInt("GrindProgress", grindTicksAccumulated);
        tag.putBoolean("Grinding", grinding);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        storedItem = tag.contains("StoredItem")
                ? ItemStack.parse(registries, tag.getCompound("StoredItem")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
        grindTicksAccumulated = tag.getInt("GrindProgress");
        grinding = tag.getBoolean("Grinding");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        loadAdditional(tag, registries);
    }

    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }


}