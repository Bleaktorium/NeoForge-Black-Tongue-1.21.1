package net.bleaktorium.black_tongue.item.custom;

import net.bleaktorium.black_tongue.block.ModBlocks;
import net.bleaktorium.black_tongue.coven.AmuletBinding;
import net.bleaktorium.black_tongue.coven.FallenWitchesData;
import net.bleaktorium.black_tongue.coven.ModDataComponents;
import net.bleaktorium.black_tongue.entity.ModEntities;
import net.bleaktorium.black_tongue.entity.custom.CovenMotherEntity;
import net.bleaktorium.black_tongue.entity.custom.CovenlessWitchEntity;
import net.bleaktorium.black_tongue.entity.custom.WitchIdentity;
import net.bleaktorium.black_tongue.entity.custom.WitchIdentityPool;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import org.jetbrains.annotations.Nullable;

public class CovenSummoningAmuletItem extends Item {

    private static final int ROOM_RADIUS = 5; // RADIUS OF THE ROOM
    private static final int REQUIRED_BOOKSHELVES = 6;

    public CovenSummoningAmuletItem(Properties properties) {

        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) return InteractionResult.SUCCESS;

        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos().above();

        AmuletBinding binding = context.getItemInHand().get(ModDataComponents.AMULET_BINDING.get());
        if (binding == null) {
            if (player != null) player.displayClientMessage(Component.literal("This amulet is bound to nothing."), true);
            return InteractionResult.FAIL;
        }

        switch (binding.type()) {
            case COVEN_MOTHER -> summonCovenMother(level, pos, player);
            case COVENLESS_WITCH -> {
                if (binding.witchName().isPresent()) {
                    summonCovenlessWitch(level, pos, binding, context.getItemInHand(), player);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    private void summonCovenMother(Level level, BlockPos pos, Player player) {
        BlockPos thronePos = findCovenThrone(level, pos);
        if (thronePos == null) {
            if (player != null) {
                player.displayClientMessage(Component.literal(
                        "This place lacks what a Coven Mother needs — a throne, and shelves enough to hold her knowledge."), true);
            }
            return;
        }

        AABB nearbyArea = new AABB(pos).inflate(ROOM_RADIUS * 2);
        boolean alreadyPresent = !level.getEntitiesOfClass(CovenMotherEntity.class, nearbyArea).isEmpty();
        if (alreadyPresent) {
            if (player != null) player.displayClientMessage(Component.literal("She is already here."), true);
            return;
        }

        CovenMotherEntity yaga = ModEntities.COVEN_MOTHER.get().create(level);
        if (yaga == null) return;

        yaga.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        yaga.setPersistenceRequired();
        yaga.setThroneCenter(thronePos);
        level.addFreshEntity(yaga);
    }

    private void summonCovenlessWitch(Level level, BlockPos pos, AmuletBinding binding, ItemStack amulet, @Nullable Player player) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        String witchName = binding.witchName().get();

        if (binding.soulId().isPresent() && FallenWitchesData.get(serverLevel.getServer()).isFallen(binding.soulId().get())) {
            amulet.remove(ModDataComponents.AMULET_BINDING.get());
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.PLAYERS, 1.0F, 0.5F);
            if (player != null) {
                player.displayClientMessage(Component.literal("The amulet goes cold. " + witchName + " will not answer again."), true);
            }
            return;
        }

        CovenlessWitchEntity witch = ModEntities.COVENLESS_WITCH.get().create(level);
        if (witch == null) return;

        witch.setIdentity(WitchIdentityPool.getByName(witchName));

        UUID soul = binding.soulId().orElseGet(UUID::randomUUID);
        witch.setSoulId(soul);
        if (binding.soulId().isEmpty()) {
            amulet.set(ModDataComponents.AMULET_BINDING.get(),
                    new AmuletBinding(binding.type(), binding.witchName(), Optional.of(soul)));
        }

        witch.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        witch.startDespawnCountdown(20 * 60);
        level.addFreshEntity(witch);
    }

    private BlockPos findCovenThrone(Level level, BlockPos center) {
        BlockPos thronePos = null;
        int bookshelfCount = 0;

        BlockPos min = center.offset(-ROOM_RADIUS, -ROOM_RADIUS, -ROOM_RADIUS);
        BlockPos max = center.offset(ROOM_RADIUS, ROOM_RADIUS, ROOM_RADIUS);

        for (BlockPos checkPos : BlockPos.betweenClosed(min, max)) {
            var block = level.getBlockState(checkPos).getBlock();
            if (block == ModBlocks.COVEN_THRONE.get()) thronePos = checkPos.immutable();
            if (block == Blocks.BOOKSHELF) bookshelfCount++;
        }

        return bookshelfCount >= REQUIRED_BOOKSHELVES ? thronePos : null;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        AmuletBinding binding = stack.get(ModDataComponents.AMULET_BINDING.get());

        if (binding == null) {
            tooltip.add(Component.literal("Unbound").withStyle(ChatFormatting.RED));
            tooltip.add(Component.literal("It answers to no one.").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.literal("Bound to: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(binding.displayName()).withStyle(ChatFormatting.GOLD)));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}