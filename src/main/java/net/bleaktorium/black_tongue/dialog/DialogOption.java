package net.bleaktorium.black_tongue.dialog;

import net.minecraft.server.level.ServerPlayer;
import java.util.function.Function;
import java.util.function.Predicate;

public record DialogOption(String label, Function<ServerPlayer, DialogNode> action,
                           Predicate<ServerPlayer> visibleIf, boolean pushesHistory) {

    public DialogOption(String label, Function<ServerPlayer, DialogNode> action) {
        this(label, action, null, true);
    }

    public DialogOption(String label, Function<ServerPlayer, DialogNode> action, Predicate<ServerPlayer> visibleIf) {
        this(label, action, visibleIf, true);
    }

    public static DialogOption continuation(String label, Function<ServerPlayer, DialogNode> action) {
        return new DialogOption(label, action, null, false);
    }

    public static DialogOption back() {
        return new DialogOption("Back", DialogSessionManager::goBack, null, false);
    }

    public boolean isVisible(ServerPlayer player) {
        return visibleIf == null || visibleIf.test(player);
    }
}