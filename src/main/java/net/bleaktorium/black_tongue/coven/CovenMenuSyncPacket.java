package net.bleaktorium.black_tongue.coven;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record CovenMenuSyncPacket(boolean canEdit, String covenName, CovenMember mother, CovenMember founder,
                                  List<CovenMember> extras, List<CovenMember> candidates) implements CustomPacketPayload {

    public static final Type<CovenMenuSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("black_tongue", "coven_menu_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CovenMenuSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, CovenMenuSyncPacket::canEdit,
            ByteBufCodecs.stringUtf8(64), CovenMenuSyncPacket::covenName,
            CovenMember.STREAM_CODEC, CovenMenuSyncPacket::mother,
            CovenMember.STREAM_CODEC, CovenMenuSyncPacket::founder,
            CovenMember.STREAM_CODEC.apply(ByteBufCodecs.list(32)), CovenMenuSyncPacket::extras,
            CovenMember.STREAM_CODEC.apply(ByteBufCodecs.list(32)), CovenMenuSyncPacket::candidates,
            CovenMenuSyncPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}