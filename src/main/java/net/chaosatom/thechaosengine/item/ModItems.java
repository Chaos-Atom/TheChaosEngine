package net.chaosatom.thechaosengine.item;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TheChaosEngine.MOD_ID);

    // Tier 1 Ore Processing
    public static final DeferredItem<Item> IRON_DUST = ITEMS.register("iron_dust",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_DUST = ITEMS.register("gold_dust",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_DUST = ITEMS.register("copper_dust",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COAL_DUST = ITEMS.register("coal_dust",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LAPIS_LAZULI_DUST = ITEMS.register("lapis_lazuli_dust",
            () -> new Item(new Item.Properties()));

    // Tier 2 Ore Processing
    public static final DeferredItem<Item> IRON_CHUNK = ITEMS.register("iron_chunk",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_CHUNK = ITEMS.register("gold_chunk",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_CHUNK = ITEMS.register("copper_chunk",
            () -> new Item(new Item.Properties()));

    // Tier 3 Ore Processing
    public static final DeferredItem<Item> ENCHANTED_IRON_SHARDS = ITEMS.register("enchanted_iron_shards",
            () -> new Item(new Item.Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final DeferredItem<Item> ENCHANTED_GOLD_SHARDS = ITEMS.register("enchanted_gold_shards",
            () -> new Item(new Item.Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    public static final DeferredItem<Item> ENCHANTED_COPPER_SHARDS = ITEMS.register("enchanted_copper_shards",
            () -> new Item(new Item.Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

    // Tier 4 Ore Processing
    public static final DeferredItem<Item> PURIFIED_CRYSTAL_IRON = ITEMS.register("purified_crystal_iron",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PURIFIED_CRYSTAL_GOLD = ITEMS.register("purified_crystal_gold",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PURIFIED_CRYSTAL_COPPER = ITEMS.register("purified_crystal_copper",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> CRYSTAL_IRON = ITEMS.register("crystal_iron",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CRYSTAL_GOLD = ITEMS.register("crystal_gold",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CRYSTAL_COPPER = ITEMS.register("crystal_copper",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> IRON_NANOPARTICLE = ITEMS.register("iron_nanoparticle",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_NANOPARTICLE = ITEMS.register("gold_nanoparticle",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> COPPER_NANOPARTICLE = ITEMS.register("copper_nanoparticle",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}