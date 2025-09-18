package net.chaosatom.thechaosengine.datagen;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.ModBlocks;
import net.chaosatom.thechaosengine.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // List<ItemLike> BAUXITE_STONE_SMELTABLES = List.of()

        // Shaped Recipes
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.IRON_DUST_BLOCK.get())
                .pattern("III")
                .pattern("III")
                .pattern("III")
                .define('I', ModItems.IRON_DUST.get())
                .unlockedBy("has_iron_dust", has(ModItems.IRON_DUST.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GOLD_DUST_BLOCK.get())
                .pattern("GGG")
                .pattern("GGG")
                .pattern("GGG")
                .define('G', ModItems.GOLD_DUST.get())
                .unlockedBy("has_gold_dust", has(ModItems.GOLD_DUST.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.COPPER_DUST_BLOCK.get())
                .pattern("CCC")
                .pattern("CCC")
                .pattern("CCC")
                .define('C', ModItems.COPPER_DUST.get())
                .unlockedBy("has_copper_dust", has(ModItems.COPPER_DUST.get())).save(recipeOutput);

        // Shapeless Recipes
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.IRON_DUST.get(), 9)
                .requires(ModBlocks.IRON_DUST_BLOCK.get())
                .unlockedBy("has_iron_dust_block", has(ModBlocks.IRON_DUST_BLOCK.get())).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GOLD_DUST.get(), 9)
                .requires(ModBlocks.GOLD_DUST_BLOCK.get())
                .unlockedBy("has_iron_dust_block", has(ModBlocks.GOLD_DUST_BLOCK.get())).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.COPPER_DUST.get(), 9)
                .requires(ModBlocks.COPPER_DUST_BLOCK.get())
                .unlockedBy("has_iron_dust_block", has(ModBlocks.COPPER_DUST_BLOCK.get())).save(recipeOutput);

        // Ore Smelting
        oreSmeltingSingle(recipeOutput, ModItems.IRON_DUST.get(), RecipeCategory.MISC, Items.IRON_INGOT,
                0.7f, 180, "iron_ingot");
        oreSmeltingSingle(recipeOutput, ModItems.GOLD_DUST.get(), RecipeCategory.MISC, Items.GOLD_INGOT,
                0.7f, 180, "gold_ingot");
        oreSmeltingSingle(recipeOutput, ModItems.COPPER_DUST.get(), RecipeCategory.MISC, Items.COPPER_INGOT,
                0.7f, 180, "copper_ingot");

        // Ore Blasting
        oreBlastingSingle(recipeOutput, ModItems.IRON_DUST.get(), RecipeCategory.MISC, Items.IRON_INGOT,
                0.7f, 90, "iron_ingot");
        oreBlastingSingle(recipeOutput, ModItems.GOLD_DUST.get(), RecipeCategory.MISC, Items.GOLD_INGOT,
                0.7f, 90, "gold_ingot");
        oreBlastingSingle(recipeOutput, ModItems.COPPER_DUST.get(), RecipeCategory.MISC, Items.COPPER_INGOT,
                0.7f, 90, "copper_ingot");

        // Ore Pulverizing


    }

    protected static void oreSmelting(RecipeOutput pRecipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.smelting(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime)
                    .group(pGroup)
                    .unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pRecipeOutput, TheChaosEngine.MOD_ID + ":" + getItemName(pResult) + "_from_smelting_" + getItemName(itemlike));
        }
    }

    protected static void oreBlasting(RecipeOutput pRecipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.blasting(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime)
                    .group(pGroup)
                    .unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pRecipeOutput, TheChaosEngine.MOD_ID + ":" + getItemName(pResult) + "_from_blasting_" + getItemName(itemlike));
        }
    }

    protected static void oreSmeltingSingle(RecipeOutput recipeOutput, ItemLike ingredients, RecipeCategory category, ItemLike result,
                                      float experience, int cookingTime, String group) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredients), category, result, experience, cookingTime)
                .group(group)
                .unlockedBy(getHasName(ingredients), has(ingredients))
                .save(recipeOutput, TheChaosEngine.MOD_ID + ":" + getItemName(result) + "_from_smelting_" + getItemName(ingredients));
    }

    protected static void oreBlastingSingle(RecipeOutput recipeOutput, ItemLike ingredients, RecipeCategory category, ItemLike result,
                                      float experience, int cookingTime, String group) {
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ingredients), category, result, experience, cookingTime)
                .group(group)
                .unlockedBy(getHasName(ingredients), has(ingredients))
                .save(recipeOutput, TheChaosEngine.MOD_ID + ":" + getItemName(result) + "_from_blasting_" + getItemName(ingredients));
    }
}
