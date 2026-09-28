package net.bleaktorium.black_tongue.coven;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Coven {

    public static final int MAX_EXTRA_MEMBERS = 7;
    public static final int MAX_NAME_LENGTH = 24;

    public enum AddResult { ADDED, ALREADY_MEMBER, FULL }
    public enum RemoveResult { REMOVED, NOT_FOUND, PROTECTED }

    private final UUID founderId;
    private String name;
    private final CovenMember mother;
    private final CovenMember founder;
    private final List<CovenMember> extras = new ArrayList<>();

    public Coven(UUID founderId, String name, CovenMember mother, CovenMember founder) {
        this.founderId = founderId;
        this.name = name;
        this.mother = mother;
        this.founder = founder;
    }

    public UUID founderId() { return founderId; }
    public String name() { return name; }
    public CovenMember mother() { return mother; }
    public CovenMember founder() { return founder; }
    public List<CovenMember> extras() { return Collections.unmodifiableList(extras); }

    public boolean isMember(CovenMember.Kind kind, String key) {
        if (mother.matches(kind, key) || founder.matches(kind, key)) return true;
        return extras.stream().anyMatch(m -> m.matches(kind, key));
    }

    AddResult add(CovenMember member) {
        if (isMember(member.kind(), member.key())) return AddResult.ALREADY_MEMBER;
        if (extras.size() >= MAX_EXTRA_MEMBERS) return AddResult.FULL;
        extras.add(member);
        return AddResult.ADDED;
    }

    RemoveResult remove(CovenMember.Kind kind, String key) {
        if (mother.matches(kind, key) || founder.matches(kind, key)) return RemoveResult.PROTECTED;
        return extras.removeIf(m -> m.matches(kind, key)) ? RemoveResult.REMOVED : RemoveResult.NOT_FOUND;
    }

    void rename(String newName) { this.name = newName; }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Founder", founderId);
        tag.putString("Name", name);
        tag.put("Mother", mother.toTag());
        tag.put("FounderMember", founder.toTag());
        ListTag list = new ListTag();
        for (CovenMember m : extras) list.add(m.toTag());
        tag.put("Extras", list);
        return tag;
    }

    public static Coven fromTag(CompoundTag tag) {
        Coven coven = new Coven(tag.getUUID("Founder"), tag.getString("Name"),
                CovenMember.fromTag(tag.getCompound("Mother")),
                CovenMember.fromTag(tag.getCompound("FounderMember")));
        ListTag list = tag.getList("Extras", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            coven.extras.add(CovenMember.fromTag(list.getCompound(i)));
        }
        return coven;
    }
}