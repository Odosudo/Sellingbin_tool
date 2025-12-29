package com.example.sellinggen;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(SellingGenMod.MOD_ID)
@EventBusSubscriber(modid = SellingGenMod.MOD_ID)
public class SellingGenMod {

    public static final String MOD_ID = "sellinggen";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public SellingGenMod(IEventBus modEventBus) {
        LOGGER.info("SellingGenMod initialized.");
    }

    // ------------------------------------------------------------
    // REGISTER CLIENT COMMANDS
    // ------------------------------------------------------------
    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
    Commands.literal("sellingbin")
        .then(Commands.literal("regen")
            .executes(ctx -> {
                FoodScanHandler.generateSellingBinFileClient();
                ctx.getSource().sendSuccess(
                    () -> Component.literal("§aSellingBin file regenerated!"),
                    false
                );
                return 1;
            })
        )
);

    }

    // ------------------------------------------------------------
    // ECONOMY MATH
    // ------------------------------------------------------------
    public static double computeIngredientBonus(int ingredientCount) {
        double linear = ingredientCount * 1.2;
        double log = 4 * Math.log(1 + ingredientCount);
        double t = Math.min(1.0, ingredientCount / 6.0);
        return (1 - t) * linear + t * log;
    }

    public static double computeHungerBonus(net.minecraft.world.item.Item item) {
        if (!item.components().has(net.minecraft.core.component.DataComponents.FOOD)) return 0;
        return item.components().get(net.minecraft.core.component.DataComponents.FOOD).nutrition() * 1.5;
    }

    public static int mapComplexityToCoins(double c) {
        double linear = 2 + c * 0.6;
        double log = 6 + 5 * Math.log(1 + c);
        double blendPoint = 20.0;
        double t = Math.min(1.0, c / blendPoint);
        double hybrid = (1 - t) * linear + t * log;
        double capped = Math.min(hybrid, 40.0);
        return (int) Math.round(capped);
    }

    public static double computeComplexity(net.minecraft.world.item.Item item, double baseScore, int ingredientCount) {
        double ingredientBonus = computeIngredientBonus(ingredientCount);
        double hungerBonus = computeHungerBonus(item);
        return baseScore + ingredientBonus + hungerBonus;
    }
}
