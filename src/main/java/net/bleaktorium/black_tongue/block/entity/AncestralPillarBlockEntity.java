package net.bleaktorium.black_tongue.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AncestralPillarBlockEntity extends BlockEntity implements GeoBlockEntity {
    public enum Occupant { NONE, WITCH, ANCESTOR }

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private Occupant occupant = Occupant.NONE;
    private String remainsName = "";

    public AncestralPillarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANCESTRAL_PILLAR_BE.get(), pos, state);
    }

    public Occupant getOccupant() { return occupant; }
    public String getRemainsName() { return remainsName; }
    public boolean isEmpty() { return occupant == Occupant.NONE; }

    public void bind(Occupant newOccupant, String name) {
        this.occupant = newOccupant;
        this.remainsName = name;
        setChanged();   // tells the game to save this chunk
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); // tells clients
    }

    public void clear() { bind(Occupant.NONE, ""); }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("Occupant", occupant.name());
        tag.putString("RemainsName", remainsName);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        occupant = tag.contains("Occupant") ? Occupant.valueOf(tag.getString("Occupant")) : Occupant.NONE;
        remainsName = tag.getString("RemainsName");
    }

    // sync to clients so the model can show the right bones
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}