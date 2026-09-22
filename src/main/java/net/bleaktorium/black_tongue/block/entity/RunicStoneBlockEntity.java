package net.bleaktorium.black_tongue.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RunicStoneBlockEntity extends BlockEntity {
    private ItemStack storedItem = ItemStack.EMPTY;

    public RunicStoneBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RUNIC_STONE_BE.get(), pos, state);
    }

    public ItemStack getStoredItem() {
        return storedItem;
    }

    public void setStoredItem(ItemStack stack) {
        this.storedItem = stack;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!storedItem.isEmpty()) {
            tag.put("StoredItem", storedItem.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("StoredItem")) {
            storedItem = ItemStack.parse(registries, tag.getCompound("StoredItem")).orElse(ItemStack.EMPTY);
        } else {
            storedItem = ItemStack.EMPTY;
        }
    }
}