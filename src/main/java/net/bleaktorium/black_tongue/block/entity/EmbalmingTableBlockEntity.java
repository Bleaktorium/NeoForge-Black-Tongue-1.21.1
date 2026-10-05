package net.bleaktorium.black_tongue.block.entity;

import net.bleaktorium.black_tongue.block.custom.EmbalmingTableBlock;
import net.bleaktorium.black_tongue.coven.ModDataComponents;
import net.bleaktorium.black_tongue.item.ModItems;
import net.bleaktorium.black_tongue.remains.RemainsData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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

    public enum ArmorFabric {
        SILK(ModItems.SILK_FABRIC_ROLL, ModItems.INFUSED_LEATHER, "leather",
                new String[]{"skin1", "skin2", "skin3", "skin4", "skin5", "skin6", "skin7", "skin8"},
                SoundEvents.ARMOR_EQUIP_LEATHER,
                Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS),
        COTTON(ModItems.COTTON_FABRIC_ROLL, ModItems.INFUSED_IRON_INGOT, "iron",
                new String[]{"ingot 1", "ingot_2", "ingot_3", "ingot_4", "ingot_5", "ingot_6", "ingot_7", "ingot_8"},
                SoundEvents.ARMOR_EQUIP_IRON,
                Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS),
        WISP(ModItems.WISP_FABRIC_ROLL, ModItems.INFUSED_DIAMOND, "diamond",
                new String[]{"gem1", "gem2", "gem3", "gem4", "gem5", "gem6", "gem7", "gem8"},
                SoundEvents.ARMOR_EQUIP_DIAMOND,
                Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS);

        public final Supplier<Item> roll;
        public final Supplier<Item> material;
        public final String materialGroup;
        public final String[] materialBones;
        public final Holder<SoundEvent> placeSound;
        public final Item[] armor;

        ArmorFabric(Supplier<Item> roll, Supplier<Item> material, String materialGroup, String[] materialBones,
                    Holder<SoundEvent> placeSound, Item... armor) {
            this.roll = roll;
            this.material = material;
            this.materialGroup = materialGroup;
            this.materialBones = materialBones;
            this.placeSound = placeSound;
            this.armor = armor;
        }

        @Nullable
        public static ArmorFabric fromRoll(ItemStack stack) {
            for (ArmorFabric f : values()) {
                if (stack.is(f.roll.get())) return f;
            }
            return null;
        }
    }

    public enum ArmorPiece {
        HELMET(ModItems.HELMET_LINING, "helmet", 5),
        CHESTPLATE(ModItems.CHESTPLATE_LINING, "chest", 8),
        LEGGINGS(ModItems.LEGGINGS_LINING, "leggings", 7),
        BOOTS(ModItems.BOOTS_LINING, "boots", 4);

        public final Supplier<Item> lining;
        public final String bone;
        public final int cost;

        ArmorPiece(Supplier<Item> lining, String bone, int cost) {
            this.lining = lining;
            this.bone = bone;
            this.cost = cost;
        }

        @Nullable
        public static ArmorPiece fromLining(ItemStack stack) {
            for (ArmorPiece p : values()) {
                if (stack.is(p.lining.get())) return p;
            }
            return null;
        }
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private ItemStack remains = ItemStack.EMPTY;

    public EmbalmingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EMBALMING_TABLE_BE.get(), pos, state);
    }

    public boolean isEmpty() {
        return remains.isEmpty(); }
    public ItemStack getRemains() {
        return remains; }

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
    @Nullable private ArmorFabric armorFabric = null;
    @Nullable private ArmorPiece armorPiece = null;
    private int armorMaterials = 0;

    public boolean hasArmorWork() { return armorFabric != null; }
    @Nullable public ArmorFabric armorFabric() { return armorFabric; }
    @Nullable public ArmorPiece armorPiece() { return armorPiece; }
    public int armorMaterials() { return armorMaterials; }

    public boolean canAcceptArmorPart(ItemStack stack) {
        if (!remains.isEmpty() || isPouring()) return false;
        if (armorFabric == null) return ArmorFabric.fromRoll(stack) != null;
        if (armorPiece == null) return ArmorPiece.fromLining(stack) != null;
        return stack.is(armorFabric.material.get()) && armorMaterials < armorPiece.cost;
    }

    public void addArmorPart(ServerLevel level, ItemStack stack) {
        if (armorFabric == null) {
            armorFabric = ArmorFabric.fromRoll(stack);
            level.playSound(null, worldPosition, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);
        } else if (armorPiece == null) {
            armorPiece = ArmorPiece.fromLining(stack);
            level.playSound(null, worldPosition, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.1F);
        } else {
            armorMaterials++;
            level.playSound(null, worldPosition, armorFabric.placeSound.value(), SoundSource.BLOCKS,
                    1.0F, 0.8F + 0.05F * armorMaterials);
            if (armorMaterials >= armorPiece.cost) {
                finishArmor(level);
                return;
            }
        }
        sync();
    }

    public ItemStack takeBackArmorPart() {
        ItemStack taken;
        if (armorMaterials > 0) {
            armorMaterials--;
            taken = new ItemStack(armorFabric.material.get());
        } else if (armorPiece != null) {
            taken = new ItemStack(armorPiece.lining.get());
            armorPiece = null;
        } else if (armorFabric != null) {
            taken = new ItemStack(armorFabric.roll.get());
            armorFabric = null;
        } else {
            return ItemStack.EMPTY;
        }
        sync();
        return taken;
    }

    public List<ItemStack> armorParts() {
        List<ItemStack> parts = new ArrayList<>();
        if (armorFabric == null) return parts;
        parts.add(new ItemStack(armorFabric.roll.get()));
        if (armorPiece != null) parts.add(new ItemStack(armorPiece.lining.get()));
        if (armorMaterials > 0) parts.add(new ItemStack(armorFabric.material.get(), armorMaterials));
        return parts;
    }

    private void finishArmor(ServerLevel level) {
        ItemStack armor = new ItemStack(armorFabric.armor[armorPiece.ordinal()]);
        armorFabric = null;
        armorPiece = null;
        armorMaterials = 0;
        sync();

        Vec3 c = remainsCenter();
        level.sendParticles(ParticleTypes.END_ROD, c.x, c.y + 0.2, c.z, 10, 0.3, 0.2, 0.3, 0.01);
        level.playSound(null, worldPosition, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

        ItemEntity drop = new ItemEntity(level, c.x, c.y + 0.1, c.z, armor);
        drop.setDeltaMovement(0, 0.15, 0);
        level.addFreshEntity(drop);
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
        if (armorFabric != null) tag.putString("ArmorFabric", armorFabric.name());
        if (armorPiece != null) tag.putString("ArmorPiece", armorPiece.name());
        tag.putInt("ArmorMaterials", armorMaterials);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        remains = ItemStack.parseOptional(registries, tag.getCompound("Remains"));
        pourStart = tag.contains("PourStart") ? tag.getLong("PourStart") : -1;
        pourOil = tag.contains("PourOil") ? RemainsData.DecayTier.valueOf(tag.getString("PourOil")) : null;
        armorFabric = tag.contains("ArmorFabric") ? ArmorFabric.valueOf(tag.getString("ArmorFabric")) : null;
        armorPiece = tag.contains("ArmorPiece") ? ArmorPiece.valueOf(tag.getString("ArmorPiece")) : null;
        armorMaterials = tag.getInt("ArmorMaterials");
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

    public void dripOil(Vector3d local) {
        if (level == null || level.getGameTime() == lastDripTick) return;
        lastDripTick = level.getGameTime();
        double x = worldPosition.getX() + local.x;
        double y = worldPosition.getY() + local.y;
        double z = worldPosition.getZ() + local.z;
        level.addParticle(ParticleTypes.FALLING_HONEY, x, y, z, 0, 0, 0);
    }

    private void finishPour(ServerLevel level) {
        RemainsData.DecayTier oil = pourOil;
        pourStart = -1;
        pourOil = null;

        RemainsData data = getRemainsData();
        if (data == null || oil == null) {
            sync(); return; }

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

    public boolean canWrap() {
        RemainsData data = getRemainsData();
        return data != null && data.anointedTier().isPresent() && !isPouring();
    }

    public void wrap(ServerLevel level) {
        RemainsData data = getRemainsData();
        if (data == null) return;

        ItemStack consecrated = new ItemStack(ModItems.CONSECRATED_REMAINS.get());
        consecrated.set(ModDataComponents.REMAINS_DATA.get(), data); // same name, same frozen tier
        remains = ItemStack.EMPTY;
        sync();

        Vec3 c = remainsCenter();
        level.sendParticles(ParticleTypes.POOF, c.x, c.y, c.z, 20, 0.4, 0.1, 0.2, 0.02);
        level.sendParticles(ParticleTypes.END_ROD, c.x, c.y + 0.2, c.z, 6, 0.2, 0.2, 0.2, 0.01);
        level.playSound(null, worldPosition, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);
        level.playSound(null, worldPosition, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.0F);

        ItemEntity drop = new ItemEntity(level, c.x, c.y + 0.1, c.z, consecrated);
        drop.setDeltaMovement(0, 0.15, 0);
        level.addFreshEntity(drop);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache; }
}