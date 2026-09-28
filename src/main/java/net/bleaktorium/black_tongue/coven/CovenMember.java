package net.bleaktorium.black_tongue.coven;

import net.bleaktorium.black_tongue.ritual.RitualParticipant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CovenMember(Kind kind, String key, String displayName) {

    public enum Kind { PLAYER, WITCH }

    public boolean matches(Kind otherKind, String otherKey) {
        return kind == otherKind && key.equals(otherKey);
    }

    public static CovenMember from(RitualParticipant p) {
        return new CovenMember(Kind.valueOf(p.kind().name()), p.key(), p.displayName());
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Kind", kind.name());
        tag.putString("Key", key);
        tag.putString("Name", displayName);
        return tag;
    }

    public static CovenMember fromTag(CompoundTag tag) {
        return new CovenMember(Kind.valueOf(tag.getString("Kind")), tag.getString("Key"), tag.getString("Name"));
    }

    public boolean isPlayer() {
        return kind == Kind.PLAYER; }

    public static final StreamCodec<RegistryFriendlyByteBuf, CovenMember> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, CovenMember::isPlayer,
            ByteBufCodecs.stringUtf8(64), CovenMember::key,
            ByteBufCodecs.stringUtf8(64), CovenMember::displayName,
            (isPlayer, key, name) -> new CovenMember(isPlayer ? Kind.PLAYER : Kind.WITCH, key, name));
}