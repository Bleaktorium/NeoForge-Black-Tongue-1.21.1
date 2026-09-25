package net.bleaktorium.black_tongue.dialog;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import java.util.List;


public record DialogSyncPacket(String text, String speakerName, String themeId, List<DialogOptionView> options) implements CustomPacketPayload {
    public static final Type<DialogSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("black_tongue", "dialog_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DialogSyncPacket::text,
            ByteBufCodecs.STRING_UTF8, DialogSyncPacket::speakerName,
            ByteBufCodecs.STRING_UTF8, DialogSyncPacket::themeId,
            DialogOptionView.STREAM_CODEC.apply(ByteBufCodecs.list()), DialogSyncPacket::options,
            DialogSyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}