package net.bleaktorium.black_tongue.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CropBlock;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PagankaRootCropBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private long ripenedOnDay = -1;

    public PagankaRootCropBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PAGANKA_ROOT_CROP_BE.get(), pos, state);
    }

    public int getAge() {
        return getBlockState().getValue(CropBlock.AGE);
    }

    public long getRipenedOnDay() { return ripenedOnDay; }
    public void setRipenedOnDay(long day) { ripenedOnDay = day; setChanged(); }

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
        tag.putLong("RipenedOnDay", ripenedOnDay);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ripenedOnDay = tag.contains("RipenedOnDay") ? tag.getLong("RipenedOnDay") : -1;
    }
}