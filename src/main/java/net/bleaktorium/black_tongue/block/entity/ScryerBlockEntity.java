package net.bleaktorium.black_tongue.block.entity;

import net.bleaktorium.black_tongue.alchemy.AlchemyKnowledge;
import net.bleaktorium.black_tongue.alchemy.AlchemyKnowledgeSyncPacket;
import net.bleaktorium.black_tongue.coven.ModAttachments;
import net.bleaktorium.black_tongue.cauldron.CauldronIngredientData;
import net.bleaktorium.black_tongue.cauldron.CauldronIngredients;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ScryerBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final int SCAN_TICKS = 3 * 20;
    private static final double SUCCESS_CHANCE = 0.55;

    public enum Result { NONE, ICE, COLD, LUKEWARM, WARM, HOT, FAILED }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private boolean hasWater = false;
    private ItemStack scanningItem = ItemStack.EMPTY;
    private int scanTicksRemaining = 0;
    private Result result = Result.NONE;
    private java.util.UUID lastScanningPlayer = null;
    public ItemStack getScanningItem() {
        return scanningItem; }


    public ScryerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SCRYER_BE.get(), pos, state);
    }

    public boolean hasWater() {
        return hasWater; }
    public Result getResult() {
        return result; }
    public boolean isScanning() {
        return !scanningItem.isEmpty(); }


    public void pourWater() {
        if (hasWater) return;
        hasWater = true;
        syncToClients();
    }

    public void empty() {
        hasWater = false;
        scanningItem = ItemStack.EMPTY;
        scanTicksRemaining = 0;
        result = Result.NONE;
        lastScanningPlayer = null;
        syncToClients();
    }

    public boolean beginScan(ItemStack stack, ServerPlayer player) {
        if (!hasWater || isScanning() || result != Result.NONE) return false;
        scanningItem = stack.copyWithCount(1);
        scanTicksRemaining = SCAN_TICKS;
        lastScanningPlayer = player.getUUID();
        syncToClients();
        return true;
    }

    public void tickServer(ServerLevel level) {
        if (!isScanning()) return;

        scanTicksRemaining--;
        if (scanTicksRemaining <= 0) {
            resolveScan(level);
        }
    }

    private void resolveScan(ServerLevel level) {
        var random = level.getRandom();
        boolean success = random.nextDouble() < SUCCESS_CHANCE;

        if (success) {
            CauldronIngredientData data = CauldronIngredients.get(scanningItem.getItem());
            result = data == null ? Result.FAILED : temperatureToResult(data.temperatureValue());

            if (data != null && lastScanningPlayer != null) {
                ServerPlayer player = level.getServer().getPlayerList().getPlayer(lastScanningPlayer);
                if (player != null) {
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(scanningItem.getItem());
                    AlchemyKnowledge current = player.getData(ModAttachments.ALCHEMY_KNOWLEDGE.get());
                    player.setData(ModAttachments.ALCHEMY_KNOWLEDGE.get(), current.withLearned(itemId));
                    net.bleaktorium.black_tongue.network.ModMessages.sendToPlayer(player,
                            new AlchemyKnowledgeSyncPacket(current.withLearned(itemId).knownItems().stream().toList()));
                }
            }
        } else {
            result = Result.FAILED;
        }

        scanningItem = ItemStack.EMPTY;
        syncToClients();
    }

    private static Result temperatureToResult(int temp) {
        int clamped = Math.max(-2, Math.min(2, temp));
        return switch (clamped) {
            case -2 -> Result.ICE;
            case -1 -> Result.COLD;
            case 0 -> Result.LUKEWARM;
            case 1 -> Result.WARM;
            default -> Result.HOT; // 2
        };
    }

    private void syncToClients() {
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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
        tag.putBoolean("HasWater", hasWater);
        tag.putString("Result", result.name());
        tag.putInt("ScanTicks", scanTicksRemaining);
        if (!scanningItem.isEmpty()) {
            tag.put("ScanningItem", scanningItem.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        hasWater = tag.getBoolean("HasWater");
        result = tag.contains("Result") ? Result.valueOf(tag.getString("Result")) : Result.NONE;
        scanTicksRemaining = tag.getInt("ScanTicks");
        scanningItem = tag.contains("ScanningItem")
                ? ItemStack.parse(registries, tag.getCompound("ScanningItem")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
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