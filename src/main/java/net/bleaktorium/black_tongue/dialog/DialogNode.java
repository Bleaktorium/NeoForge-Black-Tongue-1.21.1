package net.bleaktorium.black_tongue.dialog;

import net.minecraft.server.level.ServerPlayer;
import java.util.List;

public record DialogNode(String text, List<DialogOption> options) {
}