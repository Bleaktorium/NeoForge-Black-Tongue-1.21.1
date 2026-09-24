package net.bleaktorium.black_tongue.dialog;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import java.util.List;


public record DialogSyncPacket(String text, List<String> optionLabels) implements CustomPacketPayload {
    public static final Type<DialogSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("black_tongue", "dialog_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogSyncPacket> STREAM_CODEC = StreamCodec.composite(
            net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8, DialogSyncPacket::text,
            net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8.apply(net.minecraft.network.codec.ByteBufCodecs.list()), DialogSyncPacket::optionLabels,
            DialogSyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}