package net.bleaktorium.black_tongue.coven;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CovenKickPacket(boolean isPlayer, String key) implements CustomPacketPayload {

    public static final Type<CovenKickPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("black_tongue", "coven_kick"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CovenKickPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, CovenKickPacket::isPlayer,
            ByteBufCodecs.stringUtf8(64), CovenKickPacket::key,
            CovenKickPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}