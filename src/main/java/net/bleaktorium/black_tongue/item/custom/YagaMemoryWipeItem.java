package net.bleaktorium.black_tongue.item.custom;

import net.bleaktorium.black_tongue.coven.CovenPlayerData;
import net.bleaktorium.black_tongue.coven.CovenRelationshipState;
import net.bleaktorium.black_tongue.coven.CovenSync;
import net.bleaktorium.black_tongue.coven.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

// DEBUG ITEM — not meant for real players. Eating this instantly wipes your
// standing with Yaga back to a first-meeting state
public class YagaMemoryWipeItem extends Item {
    public YagaMemoryWipeItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);

        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            CovenPlayerData reset = CovenPlayerData.initial();
            player.setData(ModAttachments.COVEN_DATA.get(), reset);
            CovenSync.syncQuestToClient(player, reset); // NEW
            player.displayClientMessage(Component.literal("Your standing with Yaga has been reset."), true);
        }

        return result;
    }
}