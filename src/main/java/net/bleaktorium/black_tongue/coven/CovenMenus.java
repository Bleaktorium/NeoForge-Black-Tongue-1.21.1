package net.bleaktorium.black_tongue.coven;

import net.bleaktorium.black_tongue.Black_Tongue;
import net.bleaktorium.black_tongue.network.ModMessages;
import net.bleaktorium.black_tongue.ritual.RitualParticipant;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = Black_Tongue.MOD_ID)
public class CovenMenus {

    public enum ViewerRole { FOUNDER, MEMBER }

    private static final Map<UUID, List<CovenMember>> PENDING = new HashMap<>();

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        PENDING.clear();
    }

    public static void openEdit(ServerPlayer founder, Coven coven, List<RitualParticipant> present) {
        List<CovenMember> candidates = present.stream()
                .map(CovenMember::from)
                .filter(m -> !coven.isMember(m.kind(), m.key()))
                .toList();
        PENDING.put(founder.getUUID(), candidates);
        send(founder, coven, true, candidates);
    }

    public static void open(ServerPlayer player) {
        Coven coven = CovenSavedData.get(player.server).findContaining(player.getUUID());
        if (coven == null) {
            player.sendSystemMessage(Component.literal("You belong to no coven."));
            return;
        }

        List<CovenMember> pending = coven.founderId().equals(player.getUUID()) ? PENDING.get(player.getUUID()) : null;
        if (pending != null) {
            List<CovenMember> stillWaiting = pending.stream()
                    .filter(m -> !coven.isMember(m.kind(), m.key())).toList();
            send(player, coven, true, stillWaiting);
        } else {
            send(player, coven, false, List.of());
        }
    }

    private static void send(ServerPlayer to, Coven coven, boolean canEdit, List<CovenMember> candidates) {
        ModMessages.sendToPlayer(to, new CovenMenuSyncPacket(canEdit, coven.name(), coven.mother(),
                coven.founder(), List.copyOf(coven.extras()), candidates));
    }

    public static void handleAccept(ServerPlayer player, String requestedName, List<CovenMember> requestedExtras) {
        CovenSavedData data = CovenSavedData.get(player.server);
        Coven coven = data.getByFounder(player.getUUID());
        List<CovenMember> candidates = PENDING.get(player.getUUID());
        if (coven == null || candidates == null) return;

        List<CovenMember> wanted = new ArrayList<>();
        for (CovenMember req : requestedExtras) {
            CovenMember known = findKnown(coven, candidates, req);
            if (known == null) continue;
            if (coven.mother().matches(known.kind(), known.key())
                    || coven.founder().matches(known.kind(), known.key())) continue;
            if (wanted.stream().anyMatch(m -> m.matches(known.kind(), known.key()))) continue;
            if (wanted.size() >= Coven.MAX_EXTRA_MEMBERS) break;
            wanted.add(known);
        }

        for (CovenMember current : List.copyOf(coven.extras())) {
            if (wanted.stream().noneMatch(m -> m.matches(current.kind(), current.key()))) {
                data.removeMember(coven.founderId(), current.kind(), current.key());
            }
        }
        for (CovenMember member : wanted) {
            data.addMember(coven.founderId(), member);
        }

        String name = cleanName(requestedName);
        if (name != null && !name.equals(coven.name())) {
            data.rename(coven.founderId(), name);
        }

        PENDING.remove(player.getUUID());
        player.sendSystemMessage(Component.literal("Your coven is set: " + coven.name() + "."));
    }

    @Nullable
    private static CovenMember findKnown(Coven coven, List<CovenMember> candidates, CovenMember wanted) {
        for (CovenMember m : coven.extras()) {
            if (m.matches(wanted.kind(), wanted.key())) return m;
        }
        for (CovenMember m : candidates) {
            if (m.matches(wanted.kind(), wanted.key())) return m;
        }
        return null;
    }

    public static void openView(ServerPlayer player) {
        Coven coven = CovenSavedData.get(player.server).findContaining(player.getUUID());
        if (coven == null) {
            player.sendSystemMessage(Component.literal("You belong to no coven."));
            return;
        }
        boolean isFounder = coven.founderId().equals(player.getUUID());
        ModMessages.sendToPlayer(player, new CovenViewSyncPacket(isFounder, coven.name(), coven.mother(),
                coven.founder(), List.copyOf(coven.extras())));
    }

    public static void handleRename(ServerPlayer player, String requestedName) {
        CovenSavedData data = CovenSavedData.get(player.server);
        Coven coven = data.getByFounder(player.getUUID());
        if (coven == null) return;
        String name = cleanName(requestedName);
        if (name != null) data.rename(coven.founderId(), name);
    }

    public static void handleKick(ServerPlayer player, CovenMember.Kind kind, String key) {
        CovenSavedData data = CovenSavedData.get(player.server);
        Coven coven = data.getByFounder(player.getUUID());
        if (coven == null) return;
        data.removeMember(coven.founderId(), kind, key);
    }


    public static void handleAbandon(ServerPlayer player) {
        CovenSavedData data = CovenSavedData.get(player.server);
        Coven coven = data.getByFounder(player.getUUID());
        if (coven == null) return;
        data.delete(coven.founderId());
        player.sendSystemMessage(Component.literal("Your coven is no more."));
    }


    public static void handleLeave(ServerPlayer player) {
        CovenSavedData data = CovenSavedData.get(player.server);
        Coven coven = data.findContaining(player.getUUID());
        if (coven == null || coven.founderId().equals(player.getUUID())) return; // founders use Abandon instead
        data.removeMember(coven.founderId(), CovenMember.Kind.PLAYER, player.getUUID().toString());
        player.sendSystemMessage(Component.literal("You have left the coven."));
    }


    @Nullable
    private static String cleanName(String raw) {
        if (raw == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : raw.toCharArray()) {
            if (StringUtil.isAllowedChatCharacter(c)) sb.append(c);
        }
        String cleaned = sb.toString().strip();
        if (cleaned.isEmpty()) return null;
        return cleaned.length() > Coven.MAX_NAME_LENGTH ? cleaned.substring(0, Coven.MAX_NAME_LENGTH) : cleaned;
    }
}