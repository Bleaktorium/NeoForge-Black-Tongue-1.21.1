package net.bleaktorium.black_tongue.coven;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record CovenViewSyncPacket(boolean isFounder, String covenName, CovenMember mother,
                                  CovenMember founder, List<CovenMember> extras) implements CustomPacketPayload {

    public static final Type<CovenViewSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("black_tongue", "coven_view_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CovenViewSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, CovenViewSyncPacket::isFounder,
            ByteBufCodecs.stringUtf8(64), CovenViewSyncPacket::covenName,
            CovenMember.STREAM_CODEC, CovenViewSyncPacket::mother,
            CovenMember.STREAM_CODEC, CovenViewSyncPacket::founder,
            CovenMember.STREAM_CODEC.apply(ByteBufCodecs.list(32)), CovenViewSyncPacket::extras,
            CovenViewSyncPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}