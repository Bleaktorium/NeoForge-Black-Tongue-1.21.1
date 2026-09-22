package net.bleaktorium.black_tongue.block.entity;

import net.bleaktorium.black_tongue.cauldron.*;
import net.bleaktorium.black_tongue.ritual.RitualMath;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WitchsCauldronBlockEntity extends BlockEntity {

    private Player lastInteractingPlayer = null;

    public void setLastInteractingPlayer(Player player) {
        this.lastInteractingPlayer = player;
    }

    public enum Stage {
        ESTABLISHING_BASE, RECIPE_FIRST_HALF, RECIPE_SECOND_HALF, BREWING_WAVES, DONE
    }

    private static final int WAVE_DURATION_TICKS = 6 * 20;
    private static final int BREW_RANGE_MIN = -3;
    private static final int BREW_RANGE_MAX = 3;

    private Stage stage = Stage.ESTABLISHING_BASE;
    private boolean active = false;

    private int currentTemperature = 0;
    private int pendingDelta = 0;
    private Integer baseTemperature = null;

    private final Map<Item, Integer> pendingRecipeIngredients = new HashMap<>();
    private CauldronRecipe matchedRecipe = null;
    private String lastResult = "";

    private int waveIndex = 0;
    private int waveStartTemperature;
    private int waveTicksRemaining;
    private int waveNetThrown;
    private final List<Double> waveAccuracies = new ArrayList<>();

    private ServerBossEvent waveBossBar = null;

    public WitchsCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WITCHS_CAULDRON_BE.get(), pos, state);
    }

    public void startBrewing() { active = true; }
    public boolean isActive() { return active; }

    public boolean tryAddIngredient(Item item) {
        if (!active) return false;

        CauldronIngredientData data = CauldronIngredients.get(item);
        if (data == null) return false;

        switch (stage) {
            case ESTABLISHING_BASE -> {
                pendingDelta += data.temperatureValue();
                return true;
            }
            case RECIPE_FIRST_HALF, RECIPE_SECOND_HALF -> {
                pendingRecipeIngredients.merge(item, 1, Integer::sum);
                return true;
            }
            case BREWING_WAVES -> {
                waveNetThrown += data.temperatureValue();
                return true;
            }
            default -> { return false; }
        }
    }

    public void stir() {
        switch (stage) {
            case ESTABLISHING_BASE -> {
                currentTemperature += pendingDelta;
                pendingDelta = 0;
                if (baseTemperature == null) {
                    baseTemperature = currentTemperature;
                    stage = Stage.RECIPE_FIRST_HALF;
                    lastResult = "Base established at " + baseTemperature + ".";
                }
            }
            case RECIPE_FIRST_HALF -> {
                boolean solidsFirst = baseTemperature >= 0;
                List<CauldronRecipe> candidates = CauldronRecipes.findMatchingFirstHalf(
                        baseTemperature, pendingRecipeIngredients, solidsFirst);

                if (candidates.isEmpty()) {
                    lastResult = "The mixture rejects it. (minor backfire)";
                    resetProgress();
                } else {
                    matchedRecipe = candidates.get(0);
                    pendingRecipeIngredients.clear();
                    stage = Stage.RECIPE_SECOND_HALF;
                    lastResult = "First half accepted.";
                }
            }
            case RECIPE_SECOND_HALF -> {
                boolean solidsFirst = baseTemperature >= 0;
                Map<Item, Integer> requiredSecondHalf = solidsFirst
                        ? matchedRecipe.liquidIngredients()
                        : matchedRecipe.solidIngredients();

                if (requiredSecondHalf.equals(pendingRecipeIngredients)) {
                    pendingRecipeIngredients.clear();
                    lastResult = "Recipe confirmed! The brew begins.";
                    stage = Stage.BREWING_WAVES;
                    waveIndex = 0;
                    waveAccuracies.clear();
                    beginNextWave();
                } else {
                    lastResult = "The mixture curdles violently! (major backfire)";
                    resetProgress();
                }
            }
            case BREWING_WAVES -> evaluateWave();
            case DONE -> {  }
        }
    }

    private void beginNextWave() {
        if (waveIndex >= matchedRecipe.waveCount()) {
            finishBrewing();
            return;
        }

        hideBossBar();

        RandomSource random = this.level.getRandom();
        int candidate;
        do {
            candidate = BREW_RANGE_MIN + random.nextInt(BREW_RANGE_MAX - BREW_RANGE_MIN + 1);
        } while (candidate == baseTemperature);

        waveStartTemperature = candidate;
        currentTemperature = candidate;
        waveNetThrown = 0;
        waveTicksRemaining = WAVE_DURATION_TICKS;

        lastResult = "Wave " + (waveIndex + 1) + "/" + matchedRecipe.waveCount() +
                " — shifted to " + candidate + " (target: " + baseTemperature + ").";
    }

    private void evaluateWave() {
        int finalTemp = waveStartTemperature + waveNetThrown;
        int correctionNeeded = Math.abs(baseTemperature - waveStartTemperature);
        int actualError = Math.abs(baseTemperature - finalTemp);

        double accuracy = Math.max(0.0, 1.0 - ((double) actualError / correctionNeeded));
        waveAccuracies.add(accuracy);
        currentTemperature = finalTemp;

        lastResult = "Wave " + (waveIndex + 1) + " result: " + String.format("%.0f%%", accuracy * 100);
        waveIndex++;
        beginNextWave();
    }

    private void finishBrewing() {
        hideBossBar();

        double averageAccuracy = waveAccuracies.stream().mapToDouble(d -> d).average().orElse(0.0);
        RitualMath.RitualOutcome outcome = RitualMath.mapScoreToOutcome(averageAccuracy);

        if (this.level instanceof ServerLevel && lastInteractingPlayer != null) {
            CauldronEffects.applyBrewOutcome(outcome, lastInteractingPlayer, matchedRecipe);
        }

        stage = Stage.DONE;
        lastResult = "Brewing complete! Average accuracy: " +
                String.format("%.0f%%", averageAccuracy * 100) + " — " + outcome;
    }

    public void tickWave() {
        if (stage != Stage.BREWING_WAVES || waveTicksRemaining <= 0) {
            hideBossBar();
            return;
        }

        waveTicksRemaining--;
        updateBossBar();

        if (waveTicksRemaining <= 0) {
            evaluateWave();
        }
    }

    private void updateBossBar() {
        int currentValue = waveStartTemperature + waveNetThrown;

        if (waveBossBar == null) {
            waveBossBar = new ServerBossEvent(
                    Component.literal("Wave " + (waveIndex + 1) + "/" + matchedRecipe.waveCount() +
                            " — Now: " + currentValue + " | Target: " + baseTemperature),
                    BossEvent.BossBarColor.PURPLE,
                    BossEvent.BossBarOverlay.PROGRESS
            );
        } else {
            waveBossBar.setName(Component.literal("Wave " + (waveIndex + 1) + "/" + matchedRecipe.waveCount() +
                    " — Now: " + currentValue + " | Target: " + baseTemperature));
        }

        waveBossBar.setProgress((float) waveTicksRemaining / WAVE_DURATION_TICKS);

        AABB nearbyArea = new AABB(worldPosition).inflate(16);
        List<ServerPlayer> nearbyPlayers = level.getEntitiesOfClass(ServerPlayer.class, nearbyArea);

        for (ServerPlayer player : nearbyPlayers) {
            if (!waveBossBar.getPlayers().contains(player)) {
                waveBossBar.addPlayer(player);
            }
        }
        for (ServerPlayer player : new ArrayList<>(waveBossBar.getPlayers())) {
            if (!nearbyPlayers.contains(player)) {
                waveBossBar.removePlayer(player);
            }
        }
    }

    private void hideBossBar() {
        if (waveBossBar != null) {
            waveBossBar.removeAllPlayers();
            waveBossBar = null;
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        hideBossBar();
    }


    public void terminateBrewing() {
        if (stage != Stage.BREWING_WAVES) return; // only meaningful mid-brew
        lastResult = "The brewing was safely stopped.";
        resetProgress();
    }

    public void scrub() {
        resetProgress();
    }

    private void resetProgress() {
        hideBossBar();
        stage = Stage.ESTABLISHING_BASE;
        active = false;
        currentTemperature = 0;
        pendingDelta = 0;
        baseTemperature = null;
        pendingRecipeIngredients.clear();
        matchedRecipe = null;
        waveIndex = 0;
        waveAccuracies.clear();
    }

    public Stage getStage() { return stage; }
    public int getCurrentTemperature() { return currentTemperature; }
    public Integer getBaseTemperature() { return baseTemperature; }
    public String getLastResult() { return lastResult; }
}