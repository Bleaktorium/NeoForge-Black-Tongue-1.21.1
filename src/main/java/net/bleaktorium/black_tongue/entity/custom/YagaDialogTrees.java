package net.bleaktorium.black_tongue.entity.custom;

import net.bleaktorium.black_tongue.coven.*;
import net.bleaktorium.black_tongue.dialog.DialogNode;
import net.bleaktorium.black_tongue.dialog.DialogOption;
import net.bleaktorium.black_tongue.dialog.DialogSessionManager;
import net.bleaktorium.black_tongue.item.ModItems;
import net.bleaktorium.black_tongue.network.ModMessages;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class YagaDialogTrees {

    // NEVER_ASKED state opens here.
    public static DialogNode firstMeeting() {
        return new DialogNode(
                "A traveler, on my doorstep, offering me a gift... You are either desperate or foolish. What brings you to the likes of me?",
                List.of(
                        new DialogOption("Ask about magic", p -> askAboutMagic()),
                        new DialogOption("I would like to trade", p -> tradePlaceholder()),
                        new DialogOption("Leave", p -> null)
                )
        );
    }

    private static DialogNode askAboutMagic() {
        return new DialogNode(
                "Ask about magic",
                List.of(
                        new DialogOption("I wish to join your coven", p -> joinCovenIntro(),
                                p -> isEligibleToAsk(p)),
                        new DialogOption("I changed my mind", p -> dropQuest(p),
                                p -> p.getData(ModAttachments.COVEN_DATA.get()).state() == CovenRelationshipState.TASK_ACCEPTED),
                        new DialogOption("Ask about witchcraft", p -> askAboutWitchcraft()),
                        DialogOption.back()
                )
        );
    }

    private static boolean isEligibleToAsk(ServerPlayer player) {
        CovenRelationshipState state = player.getData(ModAttachments.COVEN_DATA.get()).state();
        return state == CovenRelationshipState.NEVER_ASKED || state == CovenRelationshipState.TASK_DECLINED;
    }

    private static DialogNode dropQuest(ServerPlayer player) {
        CovenPlayerData reset = new CovenPlayerData(CovenRelationshipState.TASK_DECLINED, List.of());
        player.setData(ModAttachments.COVEN_DATA.get(), reset);
        CovenSync.syncQuestToClient(player, reset);

        return new DialogNode(
                "Hmph. Fickle little thing, aren't you.",
                List.of(DialogOption.back())
        );
    }

    private static DialogNode askAboutWitchcraft() {
        return new DialogNode(
                "Curious aren't we? What is it that you wish to know exactly?",
                List.of(
                        new DialogOption("What kind of magic does witchcraft practice?", p -> new DialogNode(
                                "Such a direct question... The answer depends on the kind of witch you ask. I am an ancestral witch. "
                                        + "I wield ancient magic passed down by generations. My ancestors are by my side and aid me when I ask for their help. "
                                        + "But it won't be much of a family secret if I tell you everything, would it now?",
                                List.of(DialogOption.back())
                        )),
                        new DialogOption("What kind of reward does witchcraft grant?", p -> new DialogNode(
                                "I know a story of an old coven that struck a deal with one of the devils. They did his dirty work and earned themselves a powerful boon.",
                                List.of(DialogOption.continuation("Next", p2 -> new DialogNode(
                                        "Little did the devil know, that coven was not interested in being a pawn and turned to conspire against him. "
                                                + "The Devil eventually found out about this betrayal. His ire blindly led him to his downfall. "
                                                + "They lured him into the Overworld and imprisoned him inside an obsidian stone using the very magic he gifted to the Coven. "
                                                + "Alone a witch would simply never even think about doing something like that. But together, who knows where the limit is...",
                                        List.of(
                                                new DialogOption("Where is this coven now?", p3 -> new DialogNode(
                                                        "Their story did not end well, dear.",
                                                        List.of(DialogOption.back())
                                                )),
                                                DialogOption.back()
                                        )
                                )))
                        )),
                        DialogOption.back()
                )
        );
    }

    // Stand-in
    private static DialogNode tradePlaceholder() {
        return null;
    }

    public static DialogNode joinCovenIntro() {
        return new DialogNode(
                "Is that so? You will need more than a gift to earn that kind of trust. "
                        + "Let's see if you have what it takes to even stand in my shadow...",
                List.of(DialogOption.continuation("Next", p -> offerTask()))
        );
    }

    private static DialogNode offerTask() {
        return new DialogNode(
                "How about this, I task you to bring me a potion. Succeed and I will see about your request. "
                        + "Fail, and well... I guess we will find out when we get there...",
                List.of(
                        new DialogOption("Agree", p -> {
                            List<ResourceLocation> assigned = CovenPotionPool.rollThree();
                            CovenPlayerData newData = new CovenPlayerData(CovenRelationshipState.TASK_ACCEPTED, assigned);
                            p.setData(ModAttachments.COVEN_DATA.get(), newData);
                            CovenSync.syncQuestToClient(p, newData); // NEW
                            return null;
                        }),
                        new DialogOption("Deny", p -> {
                            CovenPlayerData newData = new CovenPlayerData(CovenRelationshipState.TASK_DECLINED, List.of());
                            p.setData(ModAttachments.COVEN_DATA.get(), newData);
                            CovenSync.syncQuestToClient(p, newData); // NEW
                            return null;
                        })
                )
        );
    }

    public static DialogNode followUpA() {
        return new DialogNode(
                "Have you brought me a potion?",
                List.of(
                        new DialogOption("Hand in the potion", YagaDialogTrees::handInPotion,
                                CovenDialogChecks::isHoldingAssignedPotion),
                        new DialogOption("Ask about magic", p -> askAboutMagic()),
                        new DialogOption("I would like to trade", p -> tradePlaceholder()),
                        new DialogOption("Leave", p -> null)
                )
        );
    }

    public static DialogNode followUpB() {
        return new DialogNode(
                "So, apple didn't roll away when it fell I see... Speak.",
                List.of(
                        new DialogOption("Ask about magic", p -> askAboutMagic()),
                        new DialogOption("I would like to trade", p -> tradePlaceholder()),
                        new DialogOption("Leave", p -> null)
                )
        );
    }

    private static DialogNode handInPotion(ServerPlayer player) {
        ItemStack potion = CovenDialogChecks.findAssignedPotionInHand(player);
        potion.shrink(1);

        CovenPlayerData current = player.getData(ModAttachments.COVEN_DATA.get());
        // POTION_DELIVERED now correctly means "gave the potion, still owes the
        // journal" — this is the state a LATER visit (after leaving mid-chain)
        // will route through, landing on followUpD instead of repeating this.
        CovenPlayerData newData = new CovenPlayerData(CovenRelationshipState.POTION_DELIVERED, current.assignedPotions());
        player.setData(ModAttachments.COVEN_DATA.get(), newData);
        CovenSync.syncQuestToClient(player, newData); // quest resolved, tracker clears

        return new DialogNode(
                "Oh my, what a fine little bottle you got there for me! I expected you to run off and never come back... You are a determined one.",
                List.of(DialogOption.continuation("Next", p -> amuletDropNode(p)))
        );
    }

    private static DialogNode amuletDropNode(ServerPlayer player) {
        // "coven mother drops summoning amulet bound to her at player feet"
        ItemStack amulet = new ItemStack(ModItems.YAGA_SUMMONING_AMULET.get());
        ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), amulet);
        player.level().addFreshEntity(itemEntity);

        return new DialogNode(
                "I see your potential. You have a knack for the spiritual and the unnatural. We can turn this spark of yours into true power. "
                        + "I shall join you and become your teacher. Return home and prepare a room for me. When my lodgings are ready, use this little trinket to summon me and I shall appear.",
                List.of(DialogOption.continuation("Next", p -> journalAskNode()))
        );
    }

    private static DialogNode journalAskNode() {
        return new DialogNode(
                "One more thing. This little journal of yours... You will need more than a diary of a dead man to aid you in this journey. You need a proper grimoire.",
                List.of(
                        new DialogOption("Hand her the Ancient Journal", p -> openJournalTrade(p),
                                CovenDialogChecks::isHoldingAncientJournal),
                        new DialogOption("I don't have it on me right now", p -> new DialogNode(
                                "Then bring it here, you are going to need it.",
                                List.of(new DialogOption("Leave", p2 -> null))
                        ))
                )
        );
    }

    private static DialogNode openJournalTrade(ServerPlayer player) {
        return new DialogNode(
                "(the journal trade isn't wired up yet — coming next)",
                List.of(new DialogOption("Leave", p -> null))
        );
    }

    public static DialogNode followUpD() {
        return new DialogNode(
                "Yes?",
                List.of(
                        new DialogOption("Hand in the Ancient Journal", p -> openJournalTrade(p),
                                CovenDialogChecks::isHoldingAncientJournal),
                        new DialogOption("I would like to trade", p -> tradePlaceholder()),
                        new DialogOption("Leave", p -> null)
                )
        );
    }

    private static void syncQuestToClient(ServerPlayer player, CovenPlayerData data) {
        boolean active = data.state() == CovenRelationshipState.TASK_ACCEPTED;
        ModMessages.sendToPlayer(player, new CovenQuestSyncPacket(active, active ? data.assignedPotions() : List.of()));
    }
}