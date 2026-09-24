package net.bleaktorium.black_tongue.dialog;

import net.minecraft.server.level.ServerPlayer;
import java.util.function.Function;
import java.util.function.Predicate;


public record DialogOption(String label, Function<ServerPlayer, DialogNode> action, Predicate<ServerPlayer> visibleIf) {

    public DialogOption(String label, Function<ServerPlayer, DialogNode> action) {
        this(label, action, null);
    }

    public boolean isVisible(ServerPlayer player) {
        return visibleIf == null || visibleIf.test(player);
    }
}