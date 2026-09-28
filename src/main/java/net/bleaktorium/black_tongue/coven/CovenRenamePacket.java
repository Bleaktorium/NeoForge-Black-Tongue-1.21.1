package net.bleaktorium.black_tongue.coven;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CovenRenamePacket(String name) implements CustomPacketPayload {

    public static final Type<CovenRenamePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("black_tongue", "coven_rename"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CovenRenamePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(64), CovenRenamePacket::name,
            CovenRenamePacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}