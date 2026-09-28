package net.bleaktorium.black_tongue.coven;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class CovenSavedData extends SavedData {

    private static final String NAME = "black_tongue_covens";

    private final Map<UUID, Coven> covensByFounder = new LinkedHashMap<>();


    public static CovenSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(CovenSavedData::new, CovenSavedData::load), NAME);
    }

    public static CovenSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        CovenSavedData data = new CovenSavedData();
        ListTag list = tag.getList("Covens", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            Coven coven = Coven.fromTag(list.getCompound(i));
            data.covensByFounder.put(coven.founderId(), coven);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Coven coven : covensByFounder.values()) list.add(coven.toTag());
        tag.put("Covens", list);
        return tag;
    }

    @Nullable
    public Coven getByFounder(UUID founderId) {
        return covensByFounder.get(founderId);
    }

    @Nullable
    public Coven findContaining(UUID playerId) {
        for (Coven coven : covensByFounder.values()) {
            if (coven.isMember(CovenMember.Kind.PLAYER, playerId.toString())) return coven;
        }
        return null;
    }

    public void create(Coven coven) {
        covensByFounder.put(coven.founderId(), coven);
        setDirty(); // without this the change never gets written
    }

    public boolean delete(UUID founderId) {
        boolean removed = covensByFounder.remove(founderId) != null;
        if (removed) setDirty();
        return removed;
    }

    @Nullable
    public Coven.AddResult addMember(UUID founderId, CovenMember member) {
        Coven coven = covensByFounder.get(founderId);
        if (coven == null) return null;
        Coven.AddResult result = coven.add(member);
        if (result == Coven.AddResult.ADDED) setDirty();
        return result;
    }

    @Nullable
    public Coven.RemoveResult removeMember(UUID founderId, CovenMember.Kind kind, String key) {
        Coven coven = covensByFounder.get(founderId);
        if (coven == null) return null;
        Coven.RemoveResult result = coven.remove(kind, key);
        if (result == Coven.RemoveResult.REMOVED) setDirty();
        return result;
    }

    public boolean rename(UUID founderId, String newName) {
        Coven coven = covensByFounder.get(founderId);
        if (coven == null) return false;
        coven.rename(newName);
        setDirty();
        return true;
    }
}