package net.bleaktorium.black_tongue.alchemy;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.coven.ModAttachments;
import net.bleaktorium.black_tongue.network.ModMessages;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = Black_Tongue.MOD_ID)
public class AlchemyLoginSync {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AlchemyKnowledge data = player.getData(ModAttachments.ALCHEMY_KNOWLEDGE.get());
            ModMessages.sendToPlayer(player, new AlchemyKnowledgeSyncPacket(data.knownItems().stream().toList()));
        }
    }
}