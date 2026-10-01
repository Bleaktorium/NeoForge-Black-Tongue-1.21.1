package net.bleaktorium.black_tongue.block.entity;

import net.bleaktorium.black_tongue.farming.DryingProfile;
import net.bleaktorium.black_tongue.farming.HerbDryingProfiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class DryingRackBlockEntity extends BlockEntity implements GeoBlockEntity, Container {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final ItemStack[] slots = { ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY };
    private final int[] elapsedTicks = { 0, 0, 0 };

    private boolean useAltTexture = false;

    public DryingRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRYING_RACK_BE.get(), pos, state);
    }

    public boolean useAltTexture() { return useAltTexture; }

    public void rollTextureVariant(net.minecraft.util.RandomSource random) {
        this.useAltTexture = random.nextBoolean();
        syncToClients();
    }

    private void onSlotChanged(int slot) {
        elapsedTicks[slot] = 0;
    }

    public void tickServer() {
        boolean changed = false;
        for (int i = 0; i < 3; i++) {
            if (slots[i].isEmpty()) continue;
            DryingProfile profile = HerbDryingProfiles.get(slots[i].getItem());
            if (profile == null) continue;

            int maxTicks = profile.stageCount() * HerbDryingProfiles.TICKS_PER_STAGE;
            if (elapsedTicks[i] < maxTicks) {
                elapsedTicks[i]++;
                changed = true;

                if (elapsedTicks[i] == maxTicks) {
                    slots[i] = profile.stageResults().get(profile.stageCount() - 1).copy();
                }
            }
        }
        if (changed) syncToClients();
    }

    private void syncToClients() {
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public int getContainerSize() { return 3; }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : slots) if (!s.isEmpty()) return false;
        return true;
    }

    @Override
    public ItemStack getItem(int slot) { return slots[slot]; }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = net.minecraft.world.ContainerHelper.removeItem(java.util.List.of(slots), slot, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = slots[slot];
        slots[slot] = ItemStack.EMPTY;
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        slots[slot] = stack;
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        onSlotChanged(slot);
        setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return 1; }

    @Override
    public void setChanged() {
        super.setChanged();
        syncToClients();
    }

    @Override
    public boolean stillValid(Player player) {
        return net.minecraft.world.Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < 3; i++) {
            slots[i] = ItemStack.EMPTY;
            elapsedTicks[i] = 0;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("AltTexture", useAltTexture);
        ListTag list = new ListTag();
        for (int i = 0; i < 3; i++) {
            CompoundTag slotTag = new CompoundTag();
            if (!slots[i].isEmpty()) slotTag.put("Item", slots[i].save(registries));
            slotTag.putInt("Elapsed", elapsedTicks[i]);
            list.add(slotTag);
        }
        tag.put("Slots", list);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        useAltTexture = tag.getBoolean("AltTexture");
        ListTag list = tag.getList("Slots", net.minecraft.nbt.Tag.TAG_COMPOUND);
        for (int i = 0; i < 3 && i < list.size(); i++) {
            CompoundTag slotTag = list.getCompound(i);
            slots[i] = slotTag.contains("Item")
                    ? ItemStack.parse(registries, slotTag.getCompound("Item")).orElse(ItemStack.EMPTY)
                    : ItemStack.EMPTY;
            elapsedTicks[i] = slotTag.getInt("Elapsed");
        }
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
}