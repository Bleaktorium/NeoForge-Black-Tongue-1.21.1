package net.bleaktorium.black_tongue.coven;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = Black_Tongue.MOD_ID)
public class CovenLoginSync {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CovenPlayerData data = player.getData(ModAttachments.COVEN_DATA.get());
            CovenSync.syncQuestToClient(player, data);
        }
    }
}