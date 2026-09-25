package net.bleaktorium.black_tongue.dialog;

import net.minecraft.server.level.ServerPlayer;
import java.util.function.Function;
import java.util.function.Predicate;

public record DialogOption(String label, Function<ServerPlayer, DialogNode> action,
                           Predicate<ServerPlayer> visibleIf, boolean pushesHistory, boolean marksQuest) {

    public DialogOption(String label, Function<ServerPlayer, DialogNode> action) {
        this(label, action, null, true, false);
    }

    public DialogOption(String label, Function<ServerPlayer, DialogNode> action, Predicate<ServerPlayer> visibleIf) {
        this(label, action, visibleIf, true, false);
    }

    public static DialogOption continuation(String label, Function<ServerPlayer, DialogNode> action) {
        return new DialogOption(label, action, null, false, false);
    }

    public static DialogOption back() {
        return new DialogOption("Back", DialogSessionManager::goBack, null, false, false);
    }

    public static DialogOption quest(DialogOption base) {
        return new DialogOption(base.label(), base.action(), base.visibleIf(), base.pushesHistory(), true);
    }

    public boolean isVisible(ServerPlayer player) {
        return visibleIf == null || visibleIf.test(player);
    }
}