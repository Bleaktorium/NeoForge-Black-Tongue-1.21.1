package net.bleaktorium.black_tongue.dialog;

import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DialogSessionManager {
    private static final Map<UUID, Deque<DialogNode>> HISTORY = new HashMap<>();
    private static final Map<UUID, DialogNode> CURRENT = new HashMap<>();

    public static void goTo(ServerPlayer player, DialogNode from, DialogNode to) {
        HISTORY.computeIfAbsent(player.getUUID(), id -> new ArrayDeque<>()).push(from);
        CURRENT.put(player.getUUID(), to);
    }

    public static DialogNode goBack(ServerPlayer player) {
        Deque<DialogNode> stack = HISTORY.get(player.getUUID());
        DialogNode previous = (stack != null && !stack.isEmpty()) ? stack.pop() : CURRENT.get(player.getUUID());
        CURRENT.put(player.getUUID(), previous);
        return previous;
    }

    public static void startSession(ServerPlayer player, DialogNode root) {
        HISTORY.put(player.getUUID(), new ArrayDeque<>());
        CURRENT.put(player.getUUID(), root);
    }

    public static void endSession(ServerPlayer player) {
        HISTORY.remove(player.getUUID());
        CURRENT.remove(player.getUUID());
    }

    public static DialogNode getCurrent(ServerPlayer player) {
        return CURRENT.get(player.getUUID());
    }
}