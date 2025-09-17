package net.chaosatom.thechaosengine.recipe;

import net.chaosatom.thechaosengine.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.HashMap;

public class FuelItemRecipes {
    // Mainly used for Compact Coal Generator
    public record FuelData(int specificBurnProgress, int specificEnergyPerTick) {
    }

    public static final HashMap<Item, FuelData> FUEL_STATS = new HashMap<>();

    static {
        FUEL_STATS.put(Items.COAL, new FuelData(110, 1));
        FUEL_STATS.put(Items.CHARCOAL, new FuelData(110, 1));
        FUEL_STATS.put(Items.COAL_BLOCK, new FuelData(1100, 1));
        FUEL_STATS.put(ModItems.COAL_DUST.get(), new FuelData(65, 2));

        FUEL_STATS.put(Items.NETHER_STAR, new FuelData(640, 50)); // Debugging Purpose
    }
}
