package net.bleaktorium.black_tongue.coven;

import net.bleaktorium.black_tongue.ritual.RitualParticipant;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class CovenService {

    public enum Status { CREATED, EXISTING, NO_COVEN_MOTHER, MEMBER_ELSEWHERE }

    public record FormationResult(Status status, @Nullable Coven coven, List<RitualParticipant> candidates) { }

    public static FormationResult formOrReopen(MinecraftServer server, UUID founderId, List<RitualParticipant> participants) {
        CovenSavedData data = CovenSavedData.get(server);

        Coven existing = data.getByFounder(founderId);
        if (existing != null) {
            return new FormationResult(Status.EXISTING, existing, candidatesFor(existing, participants));
        }

        Coven elsewhere = data.findContaining(founderId);
        if (elsewhere != null) {
            return new FormationResult(Status.MEMBER_ELSEWHERE, elsewhere, List.of());
        }

        RitualParticipant motherP = participants.stream()
                .filter(RitualParticipant::covenMotherTier).findFirst().orElse(null);
        RitualParticipant founderP = participants.stream()
                .filter(p -> p.kind() == RitualParticipant.Kind.PLAYER && p.key().equals(founderId.toString()))
                .findFirst().orElse(null);
        if (motherP == null || founderP == null) {
            return new FormationResult(Status.NO_COVEN_MOTHER, null, List.of());
        }

        Coven coven = new Coven(founderId, founderP.displayName() + "'s Coven",
                CovenMember.from(motherP), CovenMember.from(founderP));
        data.create(coven);
        return new FormationResult(Status.CREATED, coven, candidatesFor(coven, participants));
    }

    public static List<Coven> removeDeadWitch(MinecraftServer server, String witchKey) {
        CovenSavedData data = CovenSavedData.get(server);
        List<Coven> leftCovens = new ArrayList<>();
        for (Coven coven : data.findContainingWitch(witchKey)) {
            if (data.removeMember(coven.founderId(), CovenMember.Kind.WITCH, witchKey) == Coven.RemoveResult.REMOVED) {
                leftCovens.add(coven);
            }
        }
        return leftCovens;
    }

    private static List<RitualParticipant> candidatesFor(Coven coven, List<RitualParticipant> participants) {
        return participants.stream()
                .filter(p -> !coven.isMember(CovenMember.Kind.valueOf(p.kind().name()), p.key()))
                .toList();
    }
}