package net.bleaktorium.black_tongue.cauldron;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.HashMap;
import java.util.Map;

public class CauldronIngredients {
    private static final Map<Item, CauldronIngredientData> DATA = new HashMap<>();

    public static void register(Item item, int temperatureValue, IngredientState state) {
        DATA.put(item, new CauldronIngredientData(temperatureValue, state));
    }

    public static CauldronIngredientData get(Item item) {
        return DATA.get(item);
    }


    // ice=-2, cold=-1, lukewarm=0, warm=1, hot=2
    public static void bootstrap() {
        register(Items.WATER_BUCKET, 0, IngredientState.LIQUID);   // lukewarm, neutral
        register(Items.NETHER_WART, 2, IngredientState.SOLID);      // hot
        register(Items.COAL, 1, IngredientState.SOLID);             // warm
        register(Items.LAPIS_LAZULI, -2, IngredientState.SOLID);    // ice
        register(Items.SUGAR, 0, IngredientState.SOLID);            // neutral — a recipe ingredient, not a temperature-mover
        register(Items.COCOA_BEANS, 0, IngredientState.SOLID);
        register(Items.HONEY_BOTTLE, 0, IngredientState.LIQUID);
        register(Items.MILK_BUCKET, 0, IngredientState.LIQUID);
    }
}