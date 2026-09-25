package net.bleaktorium.black_tongue.coven;

public enum CovenRelationshipState {
    NEVER_ASKED,        // -> root tree (first meeting)
    TASK_DECLINED,      // -> follow-up B ("apple didn't roll away...")
    TASK_ACCEPTED,      // -> follow-up A ("have you brought me a potion?")
    POTION_DELIVERED,   // -> follow-up D (amulet given, waiting on the journal handoff)
    GRIMOIRE_RECEIVED   // -> follow-up C — the witchcraft path is now locked in
}
