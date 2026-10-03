package net.bleaktorium.black_tongue.block.entity;

import net.bleaktorium.black_tongue.block.custom.*;
import net.bleaktorium.black_tongue.coven.*;
import net.bleaktorium.black_tongue.entity.custom.WitchIdentity;
import net.bleaktorium.black_tongue.entity.custom.WitchIdentityPool;
import net.bleaktorium.black_tongue.item.ModItems;
import net.bleaktorium.black_tongue.ritual.RitualEffects;
import net.bleaktorium.black_tongue.ritual.RitualGuests;
import net.bleaktorium.black_tongue.ritual.RitualIngredients;
import net.bleaktorium.black_tongue.ritual.RitualMath;
import net.bleaktorium.black_tongue.ritual.RitualParticipant;
import net.bleaktorium.black_tongue.ritual.RitualRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RitualTableBlockEntity extends BlockEntity implements GeoBlockEntity {

    // GeckoLib
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Tuning knobs
    private static final boolean SHOW_RITUAL_MATH = true; // testing aid: math readout and recipe hints
    private static final int CAST_TICKS = 10 * 20;
    private static final double TAMPER_PENALTY = 25.0;
    private static final int GUEST_LINGER_TICKS = 15 * 20;
    private static final int GUEST_SAFETY_TICKS = 2 * 60 * 20;

    private static class Watched {
        final BlockPos pos;
        final BlockState state;
        final ItemStack item;
        final boolean offering;
        boolean tampered = false;

        Watched(BlockPos pos, BlockState state, ItemStack item, boolean offering) {
            this.pos = pos;
            this.state = state;
            this.item = item;
            this.offering = offering;
        }
    }

    private record SeatView(BlockPos pos, BlockPos offset, MoonPhase phase, ItemStack stored,
                            AmuletBinding binding, WitchIdentity witch, ServerPlayer standing) { }

    private record WitchSeat(BlockPos pos, AmuletBinding binding) { }

    private static class Guest {
        final UUID id;
        final BlockPos seatPos;
        boolean lostPenaltyApplied = false;

        Guest(UUID id, BlockPos seatPos) {
            this.id = id;
            this.seatPos = seatPos;
        }
    }

    private static class PlayerSeat {
        final UUID id;
        final BlockPos seatPos;
        boolean lostPenaltyApplied = false;

        PlayerSeat(UUID id, BlockPos seatPos) {
            this.id = id;
            this.seatPos = seatPos;
        }
    }

    private boolean casting = false;
    private int ticksElapsed = 0;
    private UUID initiatorId = null;
    private RitualRecipe activeRecipe = null;
    private int totalAmplification = 0;
    private double baseStability = 0;
    private double stabilityModifier = 0;
    private final List<Watched> watched = new ArrayList<>();
    private final List<Guest> guests = new ArrayList<>();
    private final List<PlayerSeat> playerSeats = new ArrayList<>();
    private final List<RitualParticipant> participants = new ArrayList<>();
    private ServerBossEvent bossBar = null;

    public RitualTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RITUAL_TABLE_BE.get(), pos, state);
    }

    // GeckoLib
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public boolean isCasting() { return casting; }

    public void addStabilityBonus(double amount) {
        if (casting) stabilityModifier += amount;
    }

    private double currentStability() {
        return Mth.clamp(baseStability + stabilityModifier, 0.0, 100.0);
    }

    private static WitchIdentity identityFor(AmuletBinding binding) {
        if (binding == null) return null;
        String name = switch (binding.type()) {
            case COVEN_MOTHER -> "Coven Mother Yaga";
            case COVENLESS_WITCH -> binding.witchName().orElse(null);
        };
        if (name == null) return null;

        WitchIdentity identity = WitchIdentityPool.getByName(name);
        return identity.name().equals(name) ? identity : null;
    }

    private static String witchKey(AmuletBinding binding, WitchIdentity witch) {
        return binding.soulId().map(UUID::toString).orElse(witch.name());
    }

    private static AABB seatColumn(BlockPos seat) {
        return new AABB(seat.getX(), seat.getY() + 0.9, seat.getZ(),
                seat.getX() + 1, seat.getY() + 3.0, seat.getZ() + 1);
    }

    @Nullable
    private static ServerPlayer playerStandingOn(ServerLevel level, BlockPos seat) {
        AABB column = seatColumn(seat);
        for (ServerPlayer p : level.players()) {
            if (p.isAlive() && !p.isSpectator() && column.contains(p.position())) return p;
        }
        return null;
    }

    // READING THE CIRCLE
    private List<SeatView> readSeats(ServerLevel serverLevel) {
        List<SeatView> seats = new ArrayList<>();
        for (BlockPos offset : RitualTableBlock.RING_OFFSETS) {
            BlockPos stonePos = worldPosition.offset(offset);
            BlockState stoneState = serverLevel.getBlockState(stonePos);
            if (!(stoneState.getBlock() instanceof MoonPhaseRuneBlock)) continue;
            if (!(serverLevel.getBlockEntity(stonePos) instanceof RunicStoneBlockEntity be)) continue;

            ItemStack stored = be.getStoredItem();
            AmuletBinding binding = stored.get(ModDataComponents.AMULET_BINDING.get());
            WitchIdentity witch = identityFor(binding);
            if (binding != null && binding.soulId().isPresent()
                    && FallenWitchesData.get(serverLevel.getServer()).isFallen(binding.soulId().get())) {
                witch = null;
            }
            ServerPlayer standing = stored.isEmpty() ? playerStandingOn(serverLevel, stonePos) : null;

            seats.add(new SeatView(stonePos, offset, stoneState.getValue(MoonPhaseRuneBlock.PHASE),
                    stored, binding, witch, standing));
        }
        return seats;
    }

    private static SeatView seatAt(List<SeatView> seats, BlockPos offset) {
        for (SeatView s : seats) {
            if (s.offset().equals(offset)) return s;
        }
        return null;
    }

    private static boolean seatMeets(RitualRecipe.Seat req, SeatView seat, ServerPlayer initiator) {
        if (seat.phase() != req.phase()) return false;
        return switch (req.role()) {
            case COVEN_MOTHER -> seat.witch() != null && seat.witch().covenMotherTier();
            case INITIATOR -> seat.standing() != null && seat.standing().getUUID().equals(initiator.getUUID());
        };
    }

    private static List<String> missingFor(RitualRecipe recipe, List<SeatView> seats, ServerPlayer initiator) {
        List<String> missing = new ArrayList<>();
        for (RitualRecipe.Seat req : recipe.requiredSeats()) {
            SeatView seat = seatAt(seats, req.offset());
            if (seat == null || !seatMeets(req, seat, initiator)) missing.add(req.describe());
        }
        return missing;
    }

    private static RitualRecipe chooseRecipe(List<SeatView> seats, ServerPlayer initiator) {
        for (RitualRecipe recipe : RitualRecipe.ALL) {
            if (missingFor(recipe, seats, initiator).isEmpty()) return recipe;
        }
        return RitualRecipe.OFFERING_GIFT;
    }

    private static String hintFor(List<SeatView> seats, ServerPlayer initiator) {
        if (!SHOW_RITUAL_MATH) return "";
        boolean looksLikeAttempt = seats.stream().anyMatch(s ->
                (s.witch() != null && s.witch().covenMotherTier()) || s.standing() != null);
        if (!looksLikeAttempt) return "";
        List<String> missing = missingFor(RitualRecipe.COVEN_FORMATION, seats, initiator);
        return missing.isEmpty() ? "" : "Coven Formation still needs " + String.join(" and ", missing) + ".";
    }

    // INITIATION
    public void beginRitual(ServerPlayer player) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (casting) {
            player.displayClientMessage(Component.literal("The ritual is already underway."), true);
            return;
        }

        if (!RitualTableBlock.isCoreCircleValid(level, worldPosition)) {
            strikeVisual(level, player.blockPosition());
            player.displayClientMessage(Component.literal("The circle is incomplete."), true);
            return;
        }

        List<SeatView> seats = readSeats(serverLevel);
        RitualRecipe recipe = chooseRecipe(seats, player);

        if (recipe.kind() == RitualRecipe.Kind.COVEN_FORMATION) {
            Coven other = CovenSavedData.get(serverLevel.getServer()).findContaining(player.getUUID());
            if (other != null && !other.founderId().equals(player.getUUID())) {
                player.displayClientMessage(Component.literal("You already belong to another coven."), true);
                return;
            }
        }

        int amp = RitualMath.PLAYER_BASE_AMPLIFICATION;
        double stabilitySum = RitualMath.PLAYER_BASE_STABILITY;
        int contributors = 1;
        int offerings = 0;
        List<Watched> snapshot = new ArrayList<>();
        List<WitchSeat> witchSeats = new ArrayList<>();
        List<PlayerSeat> newPlayerSeats = new ArrayList<>();
        List<RitualParticipant> people = new ArrayList<>();
        Set<String> seatedWitches = new HashSet<>();

        people.add(RitualParticipant.player(player));

        for (SeatView seat : seats) {
            snapshot.add(new Watched(seat.pos(), serverLevel.getBlockState(seat.pos()), seat.stored().copy(), false));

            String key = seat.witch() != null ? witchKey(seat.binding(), seat.witch()) : null;
            if (key != null && seatedWitches.add(key)) {
                amp += seat.witch().ritualAmplification();
                stabilitySum += seat.witch().ritualStability();
                contributors++;
                witchSeats.add(new WitchSeat(seat.pos(), seat.binding()));
                people.add(RitualParticipant.witch(seat.witch(), key));
            } else if (seat.standing() != null) {

        for (BlockPos offset : RitualTableBlock.RING_OFFSETS) {
            BlockPos stonePos = worldPosition.offset(offset);
            BlockState stoneState = level.getBlockState(stonePos);
            if (!(stoneState.getBlock() instanceof RunicStoneBlock)) continue;

            switch (stoneState.getValue(RunicStoneBlock.RUNE)) {
                case NESTING -> {
                    if (recipe.usesOfferings()
                            && level.getBlockEntity(stonePos) instanceof RunicStoneBlockEntity nestBe
                            && RitualIngredients.isValidOffering(nestBe.getStoredItem())) {
                        amp += RitualMath.NESTING_FILLED_AMPLIFICATION;
                        stabilitySum += RitualMath.NESTING_STABILITY;
                        contributors++;
                        offerings++;
                        snapshot.add(new Watched(stonePos, stoneState, nestBe.getStoredItem().copy(), true));
                    }
                }
                case POTENCY -> {
                    amp += RitualMath.POTENCY_AMPLIFICATION;
                    stabilitySum += RitualMath.POTENCY_STABILITY;
                    contributors++;
                    snapshot.add(new Watched(stonePos, stoneState, ItemStack.EMPTY, false));
                }
                case BLANK -> { }
            }
        }

        for (BlockPos offset : RitualTableBlock.PILLAR_OFFSETS) {
            BlockPos pillarPos = worldPosition.offset(offset);
            BlockState pillarState = level.getBlockState(pillarPos);
            if (!(pillarState.getBlock() instanceof AncestralPillarBlock)
                    || pillarState.getValue(AncestralPillarBlock.PART) != AncestralPillarBlock.PillarPart.BOTTOM) continue;
            if (!(level.getBlockEntity(pillarPos) instanceof AncestralPillarBlockEntity pillar) || pillar.isEmpty()) continue;

            switch (pillar.getOccupant()) {
                case ANCESTOR -> {
                    amp += RitualMath.ANCESTOR_AMPLIFICATION;
                    stabilitySum += RitualMath.ANCESTOR_STABILITY;
                }
                case WITCH -> {
                    amp += RitualMath.COVEN_REMAINS_AMPLIFICATION;
                    stabilitySum += RitualMath.COVEN_REMAINS_STABILITY;
                }
                default -> { }
            }
            contributors++;
            snapshot.add(new Watched(pillarPos, pillarState, ItemStack.EMPTY, false));
        }

        if (offerings < recipe.minOfferings()) {
            player.displayClientMessage(Component.literal("The ritual needs an offering."), true);
            String hint = hintFor(seats, player);
            if (!hint.isEmpty()) player.sendSystemMessage(Component.literal(hint));
            return;
        }
        if (amp < recipe.requiredAmplification()) {
            player.displayClientMessage(Component.literal(
                    "Not enough power (" + amp + "/" + recipe.requiredAmplification() + ")."), true);
            String hint = hintFor(seats, player);
            if (!hint.isEmpty()) player.sendSystemMessage(Component.literal(hint));
            return;
        }

        casting = true;
        ticksElapsed = 0;
        initiatorId = player.getUUID();
        activeRecipe = recipe;
        totalAmplification = amp;
        baseStability = stabilitySum / contributors;
        stabilityModifier = 0;
        watched.clear();
        watched.addAll(snapshot);
        playerSeats.clear();
        playerSeats.addAll(newPlayerSeats);
        participants.clear();
        participants.addAll(people);

        guests.clear();
        for (WitchSeat ws : witchSeats) {
            Entity guest = RitualGuests.summon(serverLevel, ws.pos().above(), worldPosition, ws.binding(), GUEST_SAFETY_TICKS);
            if (guest != null) guests.add(new Guest(guest.getUUID(), ws.pos()));
        }

        String math = SHOW_RITUAL_MATH
                ? String.format(" [power %d/%d, stability %.0f%%]", amp, recipe.requiredAmplification(), baseStability)
                : "";
        player.displayClientMessage(Component.literal("The ritual begins: " + recipe.name() + "." + math), true);
    }

    // THE CAST
    public void tickServer() {
        if (!casting || !(level instanceof ServerLevel serverLevel)) return;

        if (!RitualTableBlock.isCoreCircleValid(serverLevel, worldPosition)) {
            resolve(serverLevel, RitualMath.RitualOutcome.CRITICAL_FAILURE, " (the circle was broken)");
            return;
        }

        for (Watched w : watched) {
            if (w.tampered || !serverLevel.hasChunkAt(w.pos)) continue;
            if (hasChanged(serverLevel, w)) {
                w.tampered = true;
                stabilityModifier -= TAMPER_PENALTY;
                strikeVisual(serverLevel, w.pos); // lightning means "this is wrong"
            }
        }

        if (ticksElapsed >= 5) {
            for (Guest g : guests) {
                if (g.lostPenaltyApplied || !serverLevel.hasChunkAt(g.seatPos)) continue;
                Entity e = serverLevel.getEntity(g.id);
                if (e == null || !e.isAlive()) {
                    g.lostPenaltyApplied = true;
                    stabilityModifier -= TAMPER_PENALTY;
                    strikeVisual(serverLevel, g.seatPos);
                }
            }
        }

        for (PlayerSeat ps : playerSeats) {
            if (ps.lostPenaltyApplied || !serverLevel.hasChunkAt(ps.seatPos)) continue;
            ServerPlayer p = serverLevel.getServer().getPlayerList().getPlayer(ps.id);
            boolean stillThere = p != null && p.isAlive() && p.level() == serverLevel
                    && seatColumn(ps.seatPos).contains(p.position());
            if (!stillThere) {
                ps.lostPenaltyApplied = true;
                stabilityModifier -= TAMPER_PENALTY;
                strikeVisual(serverLevel, ps.seatPos);
            }
        }

        ticksElapsed++;
        double stability = currentStability();

        if (ticksElapsed % 20 == 0) {
            double chance = Math.max(0.0, (60.0 - stability) / 60.0) * 0.6;
            if (serverLevel.getRandom().nextDouble() < chance) {
                List<BlockPos> ring = RitualTableBlock.RING_OFFSETS;
                BlockPos target = worldPosition.offset(ring.get(serverLevel.getRandom().nextInt(ring.size())));
                strikeVisual(serverLevel, target);
            }
        }

        if (ticksElapsed % 4 == 0) {
            serverLevel.sendParticles(ParticleTypes.WITCH,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 1.2, worldPosition.getZ() + 0.5,
                    3, 0.4, 0.3, 0.4, 0.02);
        }

        updateBossBar(serverLevel);

        if (ticksElapsed >= CAST_TICKS) {
            finish(serverLevel);
        }
    }

    private boolean hasChanged(ServerLevel serverLevel, Watched w) {
        if (serverLevel.getBlockState(w.pos) != w.state) return true;
        if (w.item.isEmpty()) return false;
        ItemStack current = serverLevel.getBlockEntity(w.pos) instanceof RunicStoneBlockEntity be
                ? be.getStoredItem() : ItemStack.EMPTY;
        return !ItemStack.matches(current, w.item);
    }

    // THE FINALE
    private void finish(ServerLevel serverLevel) {
        RitualMath.RitualOutcome outcome = RitualMath.rollOutcome(currentStability(), serverLevel.getRandom());
        String note = "";

        boolean offeringMissing = false;
        for (Watched w : watched) {
            if (w.offering && offeringGone(w)) {
                offeringMissing = true;
                break;
            }
        }
        if (offeringMissing && outcome.ordinal() > RitualMath.RitualOutcome.FAILURE_WITH_SIDE_EFFECT.ordinal()) {
            outcome = RitualMath.RitualOutcome.FAILURE_WITH_SIDE_EFFECT;
            note = " (an offering was taken back)";
        }

        resolve(serverLevel, outcome, note);
    }

    private boolean offeringGone(Watched w) {
        ItemStack current = level.getBlockEntity(w.pos) instanceof RunicStoneBlockEntity be
                ? be.getStoredItem() : ItemStack.EMPTY;
        return !ItemStack.matches(current, w.item);
    }

    private void resolve(ServerLevel serverLevel, RitualMath.RitualOutcome outcome, String note) {
        ServerPlayer initiator = initiatorId == null ? null
                : serverLevel.getServer().getPlayerList().getPlayer(initiatorId);

        if (activeRecipe != null && activeRecipe.kind() == RitualRecipe.Kind.COVEN_FORMATION) {
            resolveFormation(serverLevel, outcome, initiator);
        } else {
            resolveOfferingGift(serverLevel, outcome, initiator);
        }

        switch (outcome) {
            case CRITICAL_FAILURE, FAILURE_WITH_SIDE_EFFECT -> strikeVisual(serverLevel, worldPosition);
            case CRITICAL_SUCCESS -> serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 1.2, worldPosition.getZ() + 0.5,
                    40, 0.5, 0.6, 0.5, 0.3);
            default -> { }
        }

        for (Guest g : guests) {
            RitualGuests.startLinger(serverLevel, g.id, GUEST_LINGER_TICKS);
        }

        if (initiator != null) {
            String math = SHOW_RITUAL_MATH && activeRecipe != null
                    ? String.format(" [power %d/%d, stability %.0f%%]", totalAmplification, activeRecipe.requiredAmplification(), currentStability())
                    : "";
            initiator.displayClientMessage(Component.literal("Ritual outcome: " + outcome + note + math), true);
        }

        reset();
    }

    private void resolveOfferingGift(ServerLevel serverLevel, RitualMath.RitualOutcome outcome, @Nullable ServerPlayer initiator) {
        // Only offerings still sitting where we left them get consumed.
        List<RunicStoneBlockEntity> consumable = new ArrayList<>();
        for (Watched w : watched) {
            if (!w.offering) continue;
            if (serverLevel.getBlockEntity(w.pos) instanceof RunicStoneBlockEntity stoneBe
                    && ItemStack.matches(stoneBe.getStoredItem(), w.item)) {
                consumable.add(stoneBe);
            }
        }

        ItemStack rewardTemplate = new ItemStack(ModItems.OFFERING_GIFT.get());
        RitualEffects.applyConsumableOutcome(outcome, initiator, consumable, rewardTemplate, 1, serverLevel, worldPosition);
    }

    private void resolveFormation(ServerLevel serverLevel, RitualMath.RitualOutcome outcome, @Nullable ServerPlayer initiator) {
        switch (outcome) {
            case CRITICAL_FAILURE -> RitualEffects.applyFormationBackfire(onlinePlayers(serverLevel));
            case FAILURE_WITH_SIDE_EFFECT -> { }
            default -> {
                if (initiatorId == null) return;

                CovenService.FormationResult result =
                        CovenService.formOrReopen(serverLevel.getServer(), initiatorId, participants);
                if (initiator == null) return;

                switch (result.status()) {
                    case CREATED, EXISTING -> {
                        initiator.sendSystemMessage(Component.literal(result.status() == CovenService.Status.CREATED
                                ? "Your coven binds itself." : "The coven remembers itself."));
                        CovenMenus.openEdit(initiator, result.coven(), result.candidates());
                    }
                    case MEMBER_ELSEWHERE -> initiator.sendSystemMessage(Component.literal("You already belong to another coven. Nothing binds."));
                    case NO_COVEN_MOTHER -> initiator.sendSystemMessage(Component.literal("No Coven Mother took part. Nothing binds."));
                }
            }
        }
    }

    private List<ServerPlayer> onlinePlayers(ServerLevel serverLevel) {
        List<ServerPlayer> result = new ArrayList<>();
        for (RitualParticipant p : participants) {
            if (p.kind() != RitualParticipant.Kind.PLAYER) continue;
            ServerPlayer sp = serverLevel.getServer().getPlayerList().getPlayer(UUID.fromString(p.key()));
            if (sp != null) result.add(sp);
        }
        return result;
    }

    private void reset() {
        casting = false;
        ticksElapsed = 0;
        initiatorId = null;
        activeRecipe = null;
        stabilityModifier = 0;
        watched.clear();
        guests.clear();
        playerSeats.clear();
        participants.clear();
        hideBossBar();
    }


    // CASTING BAR
    private void updateBossBar(ServerLevel serverLevel) {
        if (bossBar == null) {
            bossBar = new ServerBossEvent(Component.literal("Ritual"),
                    BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
        }
        bossBar.setProgress(Mth.clamp((float) ticksElapsed / CAST_TICKS, 0f, 1f));

        AABB area = new AABB(worldPosition).inflate(16);
        List<ServerPlayer> nearby = serverLevel.getEntitiesOfClass(ServerPlayer.class, area);
        for (ServerPlayer p : nearby) {
            if (!bossBar.getPlayers().contains(p)) bossBar.addPlayer(p);
        }
        for (ServerPlayer p : new ArrayList<>(bossBar.getPlayers())) {
            if (!nearby.contains(p)) bossBar.removePlayer(p);
        }
    }

    private void hideBossBar() {
        if (bossBar != null) {
            bossBar.removeAllPlayers();
            bossBar = null;
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        hideBossBar();
    }

    private static void strikeVisual(Level level, BlockPos pos) {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt == null) return;
        bolt.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        bolt.setVisualOnly(true);
        level.addFreshEntity(bolt);
    }
}