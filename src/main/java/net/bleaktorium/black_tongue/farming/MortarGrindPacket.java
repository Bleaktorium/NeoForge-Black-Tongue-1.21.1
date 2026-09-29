package net.bleaktorium.black_tongue.farming;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MortarGrindPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<MortarGrindPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("black_tongue", "mortar_grind"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MortarGrindPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, MortarGrindPacket::pos,
            MortarGrindPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}