package net.bleaktorium.black_tongue.coven;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record CovenQuestSyncPacket(boolean active, List<ResourceLocation> assignedPotions) implements CustomPacketPayload {
    public static final Type<CovenQuestSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("black_tongue", "coven_quest_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CovenQuestSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, CovenQuestSyncPacket::active,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), CovenQuestSyncPacket::assignedPotions,
            CovenQuestSyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}