package net.bleaktorium.black_tongue.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WitchShelfBlockEntity extends BlockEntity implements GeoBlockEntity {
    public static final int SIZE = 108; // 12 rows of 9

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int filledSlots = 0;

    private final ItemStackHandler storage = new ItemStackHandler(SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            updateFill();
        }
    };

    public WitchShelfBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WITCH_SHELF_BE.get(), pos, state);
    }

    public ItemStackHandler getStorage() { return storage; }

    // 0 = empty, 1 = up to 25%, 2 = up to 50%, 3 = up to 75%, 4 = above 75%
    public int fillStage() {
        if (filledSlots == 0) return 0;
        return Mth.ceil(filledSlots * 4.0F / SIZE);
    }

    private int countFilled() {
        int count = 0;
        for (int i = 0; i < SIZE; i++) {
            if (!storage.getStackInSlot(i).isEmpty()) count++;
        }
        return count;
    }

    private void updateFill() {
        int before = fillStage();
        filledSlots = countFilled();
        if (fillStage() != before && level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    public void dropContents(Level level, BlockPos pos) {
        for (int i = 0; i < SIZE; i++) {
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, storage.getStackInSlot(i));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Storage", storage.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Storage")) {
            storage.deserializeNBT(registries, tag.getCompound("Storage"));
            filledSlots = countFilled();
        }
        if (tag.contains("Filled")) filledSlots = tag.getInt("Filled");
    }

    // The client only needs to know how full the shelf is, not every stack.
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Filled", filledSlots);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}