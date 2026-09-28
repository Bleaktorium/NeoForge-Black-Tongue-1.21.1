package net.bleaktorium.black_tongue.dialog;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenWitchTradePacket(String witchName) implements CustomPacketPayload {
    public static final Type<OpenWitchTradePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("black_tongue", "open_witch_trade"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenWitchTradePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, OpenWitchTradePacket::witchName,
            OpenWitchTradePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}