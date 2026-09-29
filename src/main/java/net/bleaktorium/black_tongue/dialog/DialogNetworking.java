package net.bleaktorium.black_tongue.dialog;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.alchemy.AlchemyKnowledgeClientHandler;
import net.bleaktorium.black_tongue.alchemy.AlchemyKnowledgeSyncPacket;
import net.bleaktorium.black_tongue.coven.*;
import net.bleaktorium.black_tongue.farming.MortarGrindPacket;
import net.bleaktorium.black_tongue.farming.MortarGrindServerHandler;
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

        registrar.playBidirectional(
                OpenWitchTradePacket.TYPE,
                OpenWitchTradePacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        (packet, context) -> {},
                        OpenWitchTradeServerHandler::handle
                )
        );

        registrar.playBidirectional(
                CovenMenuSyncPacket.TYPE, CovenMenuSyncPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        CovenMenuClientHandler::handleSync, (packet, context) -> {})
        );

        registrar.playBidirectional(
                CovenMenuAcceptPacket.TYPE, CovenMenuAcceptPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        (packet, context) -> {}, CovenMenuServerHandler::handleAccept)
        );

        registrar.playBidirectional(
                CovenViewSyncPacket.TYPE, CovenViewSyncPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(CovenMenuClientHandler::handleViewSync,
                        (packet, context) -> {})
        );

        registrar.playBidirectional(
                CovenRenamePacket.TYPE, CovenRenamePacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        (packet, context) -> {}, CovenMenuServerHandler::handleRename)
        );

        registrar.playBidirectional(
                CovenKickPacket.TYPE, CovenKickPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        (packet, context) -> {}, CovenMenuServerHandler::handleKick)
        );

        registrar.playBidirectional(
                CovenLeaveOrAbandonPacket.TYPE, CovenLeaveOrAbandonPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        (packet, context) -> {}, CovenMenuServerHandler::handleLeaveOrAbandon)
        );

        registrar.playBidirectional(
                MortarGrindPacket.TYPE, MortarGrindPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>((packet, context) -> {}, MortarGrindServerHandler::handle));

        registrar.playBidirectional(
                AlchemyKnowledgeSyncPacket.TYPE, AlchemyKnowledgeSyncPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(AlchemyKnowledgeClientHandler::handleSync, (packet, context) -> {}));


    }

}
