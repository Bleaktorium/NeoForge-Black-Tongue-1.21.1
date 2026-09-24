package net.bleaktorium.black_tongue.dialog;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DialogClosePacket() implements CustomPacketPayload {
    public static final Type<DialogClosePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("black_tongue", "dialog_close"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DialogClosePacket> STREAM_CODEC =
            StreamCodec.unit(new DialogClosePacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}