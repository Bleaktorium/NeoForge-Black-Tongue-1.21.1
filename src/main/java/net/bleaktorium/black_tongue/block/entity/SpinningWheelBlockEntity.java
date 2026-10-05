package net.bleaktorium.black_tongue.block.entity;

import net.bleaktorium.black_tongue.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SpinningWheelBlockEntity extends BlockEntity implements GeoBlockEntity {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SPINNING = RawAnimation.begin().thenLoop("spinning");

    public static final int FIBERS_NEEDED = 4;
    public static final int SPIN_TICKS = 5 * 60 * 20; // 5 minutes

    public enum Fiber {
        SILK("silk"), COTTON("cotton"), WISP("wisp"), WOOL("wool");

        private final String textureSuffix;
        Fiber(String textureSuffix) {
            this.textureSuffix = textureSuffix; }
        public String textureSuffix() {
            return textureSuffix; }

        public boolean matches(ItemStack stack) {
            return switch (this) {
                case SILK -> stack.is(ModItems.SPIDER_SILK.get());
                case COTTON -> stack.is(ModItems.DEVILS_COTTON.get());
                case WISP -> stack.is(ModItems.BANSHEES_WISP.get());
                case WOOL -> stack.is(ItemTags.WOOL);
            };
        }

        public Item thread() {
            return switch (this) {
                case SILK -> ModItems.SPIDER_SILK_THREAD.get();
                case COTTON -> ModItems.DEVILS_COTTON_THREAD.get();
                case WISP -> ModItems.BANSHEES_WISP_THREAD.get();
                case WOOL -> ModItems.WOOL_THREAD.get();
            };
        }

        @Nullable
        public static Fiber fromInput(ItemStack stack) {
            for (Fiber fiber : values()) {
                if (fiber.matches(stack)) return fiber;
            }
            return null;
        }
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private ItemStack input = ItemStack.EMPTY; // the 4 fibers on the wheel
    private long spinStart = -1;

    public SpinningWheelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPINNING_WHEEL_BE.get(), pos, state);
    }

    @Nullable
    public Fiber getFiber() {
        return input.isEmpty() ? null : Fiber.fromInput(input);
    }

    public boolean isEmpty() {
        return input.isEmpty(); }

    public int fiberCount() {
        return input.getCount(); }

    public boolean isSpinning() {
        return spinStart >= 0 && level != null && level.getGameTime() - spinStart < SPIN_TICKS;
    }

    public boolean isReady() {
        return spinStart >= 0 && level != null && !isSpinning();
    }

    public boolean canAccept(ItemStack stack) {
        if (spinStart >= 0 || Fiber.fromInput(stack) == null) return false;
        return input.isEmpty() || ItemStack.isSameItemSameComponents(input, stack);
    }

    public void addFiber(ItemStack stack) {
        if (level == null || !canAccept(stack)) return;
        if (input.isEmpty()) {
            input = stack.copyWithCount(1);
        } else {
            input.grow(1);
        }
        if (input.getCount() >= FIBERS_NEEDED) {
            spinStart = level.getGameTime();
        }
        sync();
    }

    public ItemStack takeFibersBack() {
        if (spinStart >= 0) return ItemStack.EMPTY;
        ItemStack back = input;
        input = ItemStack.EMPTY;
        sync();
        return back;
    }

    public ItemStack collectThread() {
        Fiber fiber = getFiber();
        if (!isReady() || fiber == null) return ItemStack.EMPTY;
        ItemStack thread = new ItemStack(fiber.thread(), FIBERS_NEEDED);
        input = ItemStack.EMPTY;
        spinStart = -1;
        sync();
        return thread;
    }

    public ItemStack contentsToDrop() {
        Fiber fiber = getFiber();
        if (isReady() && fiber != null) return new ItemStack(fiber.thread(), FIBERS_NEEDED);
        return input;
    }

    private void sync() {
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Input", input.saveOptional(registries));
        tag.putLong("SpinStart", spinStart);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        input = ItemStack.parseOptional(registries, tag.getCompound("Input"));
        spinStart = tag.contains("SpinStart") ? tag.getLong("SpinStart") : -1;
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
        controllers.add(new AnimationController<>(this, "controller", 5,
                state -> state.setAndContinue(isSpinning() ? SPINNING : IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}