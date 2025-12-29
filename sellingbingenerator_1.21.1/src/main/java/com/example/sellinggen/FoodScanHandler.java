package com.example.sellinggen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FoodScanHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("sellinggen");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Type LIST_TYPE = new TypeToken<List<Map<String, Object>>>() {}.getType();

    // ------------------------------------------------------------
    // CLIENT-SIDE ENTRY POINT (COMMAND ONLY)
    // ------------------------------------------------------------
    public static void generateSellingBinFileClient() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null) {
            LOGGER.error("Minecraft client not ready — cannot scan foods.");
            return;
        }

        RecipeManager recipeManager = mc.level.getRecipeManager();
        RegistryAccess registryAccess = mc.level.registryAccess();

        // Load existing entries from selling_bin.json
        Set<String> existingFilters = loadExistingFilters();

        // New entries only
        List<Map<String, Object>> newEntries = new ArrayList<>();

        for (Item item : BuiltInRegistries.ITEM) {

            if (!item.components().has(DataComponents.FOOD)) continue;

            String id = BuiltInRegistries.ITEM.getKey(item).toString();

            // Skip if already exists in selling_bin.json
            if (existingFilters.contains(id)) continue;

            ItemStack stack = new ItemStack(item);
            FoodProperties food = stack.getFoodProperties(null);
            if (food == null) continue;

            double baseScore = computeBaseScore(food);
            int ingredientCount = getIngredientCount(item, recipeManager, registryAccess);

            double finalComplexity = SellingGenMod.computeComplexity(item, baseScore, ingredientCount);
            int coins = SellingGenMod.mapComplexityToCoins(finalComplexity);

            // Build Option A format
            Map<String, Object> entry = new LinkedHashMap<>();

            Map<String, Object> input = new LinkedHashMap<>();
            input.put("filter", id);
            input.put("count", ingredientCount);

            Map<String, Object> output = new LinkedHashMap<>();
            output.put("item", "mayview:copper_coin");
            output.put("count", coins);

            entry.put("input", input);
            entry.put("output", output);

            newEntries.add(entry);
        }

        writeFile(newEntries);
    }

    // ------------------------------------------------------------
    // LOAD EXISTING FILTERS FROM selling_bin.json
    // ------------------------------------------------------------
    private static Set<String> loadExistingFilters() {
        Path path = Path.of("config/sellingbin/selling_bin.json");
        Set<String> filters = new HashSet<>();

        if (!Files.exists(path)) {
            return filters;
        }

        try {
            String json = Files.readString(path);
            List<Map<String, Object>> list = GSON.fromJson(json, LIST_TYPE);

            if (list != null) {
                for (Map<String, Object> entry : list) {
                    Map<String, Object> input = (Map<String, Object>) entry.get("input");
                    if (input != null && input.containsKey("filter")) {
                        filters.add(input.get("filter").toString());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to read selling_bin.json", e);
        }

        return filters;
    }

    // ------------------------------------------------------------
    // BASE COMPLEXITY
    // ------------------------------------------------------------
    private static double computeBaseScore(FoodProperties food) {
        double score = 0;

        score += food.nutrition() * 1.2;
        score += food.saturation() * 2.0;

        List<MobEffectInstance> effects = food.effects().stream()
                .map(FoodProperties.PossibleEffect::effect)
                .toList();

        score += effects.size() * 3.0;

        return score;
    }

    // ------------------------------------------------------------
    // INGREDIENT COUNT (NULL-SAFE)
    // ------------------------------------------------------------
    private static int getIngredientCount(Item item, RecipeManager manager, RegistryAccess registryAccess) {
        return manager.getRecipes().stream()
                .map(RecipeHolder::value)
                .filter(r -> {
                    ItemStack result = r.getResultItem(registryAccess);
                    return result != null && !result.isEmpty() && result.getItem() == item;
                })
                .mapToInt(r -> r.getIngredients().size())
                .max()
                .orElse(0);
    }

    // ------------------------------------------------------------
    // WRITE JSON FILE (ARRAY FORMAT)
    // ------------------------------------------------------------
    private static void writeFile(List<Map<String, Object>> data) {
        try {
            Path configDir = Path.of("config/sellingbin");
            Files.createDirectories(configDir);

            Path target = configDir.resolve("sellingbin_generated.json");
            Files.writeString(target, GSON.toJson(data));

            LOGGER.info("Generated sellingbin_generated.json successfully.");
        } catch (IOException e) {
            LOGGER.error("Failed to write sellingbin_generated.json", e);
        }
    }
}
