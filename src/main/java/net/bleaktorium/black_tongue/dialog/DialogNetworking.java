package net.bleaktorium.black_tongue.dialog;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.coven.CovenQuestClientHandler;
import net.bleaktorium.black_tongue.coven.CovenQuestSyncPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Black_Tongue.MOD_ID)
public class DialogNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playBidirectional(
                DialogSyncPacket.TYPE,
                DialogSyncPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        DialogClientHandler::handleSync,
                        (packet, context) -> {}
                )
        );

        registrar.playBidirectional(
                DialogClosePacket.TYPE,
                DialogClosePacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        DialogClientHandler::handleClose,
                        (packet, context) -> {}
                )
        );

        registrar.playBidirectional(
                DialogChoicePacket.TYPE,
                DialogChoicePacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        (packet, context) -> {},
                        DialogServerHandler::handleChoice
                )
        );

        registrar.playBidirectional(
                CovenQuestSyncPacket.TYPE,
                CovenQuestSyncPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        CovenQuestClientHandler::handleSync,
                        (packet, context) -> {} // server never receives this
                )
        );
    }
}
