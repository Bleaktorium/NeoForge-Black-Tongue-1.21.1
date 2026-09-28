package net.bleaktorium.black_tongue.coven;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CovenLeaveOrAbandonPacket() implements CustomPacketPayload {

    public static final Type<CovenLeaveOrAbandonPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("black_tongue", "coven_leave_or_abandon"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CovenLeaveOrAbandonPacket> STREAM_CODEC =
            StreamCodec.unit(new CovenLeaveOrAbandonPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}