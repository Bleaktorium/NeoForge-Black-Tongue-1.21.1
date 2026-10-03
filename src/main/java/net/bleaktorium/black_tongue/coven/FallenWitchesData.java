package net.bleaktorium.black_tongue.coven;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FallenWitchesData extends SavedData {

    private static final String NAME = "black_tongue_fallen_witches";

    private final Set<UUID> fallen = new HashSet<>();

    public static FallenWitchesData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(FallenWitchesData::new, FallenWitchesData::load), NAME);
    }

    public static FallenWitchesData load(CompoundTag tag, HolderLookup.Provider registries) {
        FallenWitchesData data = new FallenWitchesData();
        ListTag list = tag.getList("Fallen", Tag.TAG_INT_ARRAY);
        for (int i = 0; i < list.size(); i++) {
            data.fallen.add(NbtUtils.loadUUID(list.get(i)));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (UUID soul : fallen) list.add(NbtUtils.createUUID(soul));
        tag.put("Fallen", list);
        return tag;
    }

    public boolean isFallen(UUID soulId) {
        return fallen.contains(soulId);
    }

    public void markFallen(UUID soulId) {
        if (fallen.add(soulId)) setDirty();
    }
}