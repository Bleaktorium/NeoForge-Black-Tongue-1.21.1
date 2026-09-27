package net.bleaktorium.black_tongue.item.custom;

import net.bleaktorium.black_tongue.block.ModBlocks;
import net.bleaktorium.black_tongue.coven.AmuletBinding;
import net.bleaktorium.black_tongue.coven.ModDataComponents;
import net.bleaktorium.black_tongue.entity.ModEntities;
import net.bleaktorium.black_tongue.entity.custom.CovenMotherEntity;
import net.bleaktorium.black_tongue.entity.custom.CovenlessWitchEntity;
import net.bleaktorium.black_tongue.entity.custom.WitchIdentity;
import net.bleaktorium.black_tongue.entity.custom.WitchIdentityPool;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import java.util.List;

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
                    summonCovenlessWitch(level, pos, binding.witchName().get());
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    private void summonCovenMother(Level level, BlockPos pos, Player player) {
        if (!hasCovenEnvironment(level, pos)) {
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
        yaga.setPersistenceRequired(); // no no to despawn
        level.addFreshEntity(yaga);
    }

    private void summonCovenlessWitch(Level level, BlockPos pos, String witchName) {
        WitchIdentity identity = WitchIdentityPool.getByName(witchName);

        CovenlessWitchEntity witch = ModEntities.COVENLESS_WITCH.get().create(level);
        if (witch == null) return;

        witch.setIdentity(identity);
        witch.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        witch.startDespawnCountdown(20 * 60); // 1 minute (2 ticks)
        level.addFreshEntity(witch);
    }

    private boolean hasCovenEnvironment(Level level, BlockPos center) {
        boolean foundThrone = false;
        int bookshelfCount = 0;

        BlockPos min = center.offset(-ROOM_RADIUS, -ROOM_RADIUS, -ROOM_RADIUS);
        BlockPos max = center.offset(ROOM_RADIUS, ROOM_RADIUS, ROOM_RADIUS);

        for (BlockPos checkPos : BlockPos.betweenClosed(min, max)) {
            var block = level.getBlockState(checkPos).getBlock();
            if (block == ModBlocks.COVEN_THRONE.get()) foundThrone = true;
            if (block == Blocks.BOOKSHELF) bookshelfCount++;
        }

        return foundThrone && bookshelfCount >= REQUIRED_BOOKSHELVES;
    }
}