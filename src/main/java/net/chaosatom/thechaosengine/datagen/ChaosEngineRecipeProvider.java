package net.chaosatom.thechaosengine.datagen;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.ChaosEngineBlocks;
import net.chaosatom.thechaosengine.item.ChaosEngineItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChaosEngineRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ChaosEngineRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // List<ItemLike> BAUXITE_STONE_SMELTABLES = List.of()

        /* SHAPED RECIPES */
        // Dust-to-Block
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineBlocks.IRON_DUST_BLOCK.get())
                .pattern("III")
                .pattern("III")
                .pattern("III")
                .define('I', ChaosEngineItems.IRON_DUST.get())
                .unlockedBy("has_iron_dust", has(ChaosEngineItems.IRON_DUST.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineBlocks.GOLD_DUST_BLOCK.get())
                .pattern("GGG")
                .pattern("GGG")
                .pattern("GGG")
                .define('G', ChaosEngineItems.GOLD_DUST.get())
                .unlockedBy("has_gold_dust", has(ChaosEngineItems.GOLD_DUST.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineBlocks.COPPER_DUST_BLOCK.get())
                .pattern("CCC")
                .pattern("CCC")
                .pattern("CCC")
                .define('C', ChaosEngineItems.COPPER_DUST.get())
                .unlockedBy("has_copper_dust", has(ChaosEngineItems.COPPER_DUST.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineBlocks.ALUMINA_BRONZE_DUST_BLOCK.get())
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ChaosEngineItems.ALUMINA_BRONZE_DUST.get())
                .unlockedBy("has_alumina_bronze_dust", has(ChaosEngineItems.ALUMINA_BRONZE_DUST.get())).save(recipeOutput);

        // Dust-to-Dust (alloying)
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineItems.ALUMINA_BRONZE_DUST.get(), 9)
                .pattern("ACC")
                .pattern("CCC")
                .pattern("CCC")
                .define('C', ChaosEngineItems.COPPER_DUST.get())
                .define('A', ChaosEngineItems.ALUMINA_DUST.get())
                .unlockedBy("has_copper_dust", has(ChaosEngineItems.COPPER_DUST.get())).save(recipeOutput);

        // Bauxite Block Variants
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineBlocks.POLISHED_BAUXITE.get(), 4)
                .pattern("BB")
                .pattern("BB")
                .define('B', ChaosEngineBlocks.BAUXITE.get())
                .unlockedBy("has_bauxite_block", has(ChaosEngineBlocks.BAUXITE.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineBlocks.BAUXITE_BRICKS.get(), 4)
                .pattern("PP")
                .pattern("PP")
                .define('P', ChaosEngineBlocks.POLISHED_BAUXITE.get())
                .unlockedBy("has_polished_bauxite", has(ChaosEngineBlocks.POLISHED_BAUXITE.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineBlocks.BAUXITE_TILES.get(), 4)
                .pattern("BB")
                .pattern("BB")
                .define('B', ChaosEngineBlocks.BAUXITE_BRICKS.get())
                .unlockedBy("has_bauxite_bricks", has(ChaosEngineBlocks.BAUXITE_BRICKS.get())).save(recipeOutput);

        // Aluminium Block Variants
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineBlocks.ALUMINIUM_BLOCK.get())
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ChaosEngineItems.ALUMINIUM_INGOT.get())
                .unlockedBy("has_aluminium_ingot", has(ChaosEngineItems.ALUMINIUM_INGOT.get())).save(recipeOutput);

        // Alumina-Bronze Block Variants
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.ALUMINA_BRONZE_BLOCK.get())
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ChaosEngineItems.ALUMINA_BRONZE_INGOT.get())
                .unlockedBy("has_aluminium_ingot", has(ChaosEngineItems.ALUMINA_BRONZE_INGOT.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS,ChaosEngineBlocks.ORNATE_ALUMINA_BRONZE.get(), 2)
                .pattern("AA")
                .define('A', ChaosEngineBlocks.ALUMINA_BRONZE_BLOCK.get())
                .unlockedBy("has_alumina_bronze_block", has(ChaosEngineBlocks.ALUMINA_BRONZE_BLOCK.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.ALUMINA_BRONZE_PANELS.get(), 2)
                .pattern("A")
                .pattern("A")
                .define('A', ChaosEngineBlocks.ALUMINA_BRONZE_BLOCK.get())
                .unlockedBy("has_alumina_bronze_block", has(ChaosEngineBlocks.ALUMINA_BRONZE_BLOCK.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.CUBIC_ALUMINA_BRONZE.get(), 2)
                .pattern("A")
                .pattern("A")
                .define('A', ChaosEngineBlocks.ALUMINA_BRONZE_PANELS.get())
                .unlockedBy("has_alumina_bronze_block", has(ChaosEngineBlocks.ALUMINA_BRONZE_PANELS.get())).save(recipeOutput);

        // Various Crafting Components
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineItems.EMPTY_BOARD.get(), 3)
                .pattern("LGL")
                .pattern("CCC")
                .define('L', ChaosEngineItems.LAPIS_LAZULI_DUST.get())
                .define('G', Items.GREEN_DYE)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_lapis_lazuli_dust", has(ChaosEngineItems.LAPIS_LAZULI_DUST.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineItems.SIMPLE_LOGIC_BOARD.get())
                .pattern(" X ")
                .pattern("IEC")
                .define('X', Items.COMPARATOR)
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COPPER_INGOT)
                .define('E', ChaosEngineItems.EMPTY_BOARD.get())
                .unlockedBy("has_empty_board", has(ChaosEngineItems.EMPTY_BOARD.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineItems.ASSEMBLY_HOUSING.get(), 4)
                .pattern("A A")
                .pattern("AAA")
                .define('A', ChaosEngineItems.ALUMINIUM_INGOT.get())
                .unlockedBy("has_aluminium_ingot", has(ChaosEngineItems.ALUMINIUM_INGOT.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineItems.LOGIC_ASSEMBLY.get())
                .pattern("GLD")
                .pattern(" A ")
                .define('L', ChaosEngineItems.SIMPLE_LOGIC_BOARD.get())
                .define('D', Items.REDSTONE)
                .define('A', ChaosEngineItems.ASSEMBLY_HOUSING.get())
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_assembly_housing", has(ChaosEngineItems.ASSEMBLY_HOUSING.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineItems.DEPLOYMENT_ASSEMBLY.get())
                .pattern("PLR")
                .pattern("DAD")
                .define('P', Items.PISTON)
                .define('R', Items.REDSTONE_TORCH)
                .define('L', ChaosEngineItems.SIMPLE_LOGIC_BOARD.get())
                .define('D', Items.REDSTONE)
                .define('A', ChaosEngineItems.ASSEMBLY_HOUSING.get())
                .unlockedBy("has_assembly_housing", has(ChaosEngineItems.ASSEMBLY_HOUSING.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineItems.HEAT_PUMP_ASSEMBLY.get())
                .pattern("CLC")
                .pattern("DAD")
                .define('C', ChaosEngineItems.COPPER_COILS)
                .define('L', ChaosEngineItems.SIMPLE_LOGIC_BOARD.get())
                .define('D', Items.REDSTONE)
                .define('A', ChaosEngineItems.ASSEMBLY_HOUSING.get())
                .unlockedBy("has_assembly_housing", has(ChaosEngineItems.ASSEMBLY_HOUSING.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineItems.COPPER_COILS.get(), 2)
                .pattern("CIC")
                .pattern("CIC")
                .pattern("CIC")
                .define('C', Items.COPPER_INGOT)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineItems.GOLD_COILS.get(), 2)
                .pattern("GIG")
                .pattern("GIG")
                .pattern("GIG")
                .define('G', Items.GOLD_INGOT)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineItems.BASIC_MACHINE_PANEL.get(), 2)
                .pattern("IA")
                .pattern("AI")
                .define('A', ChaosEngineItems.ALUMINIUM_INGOT.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_aluminium_ingot", has(ChaosEngineItems.ALUMINIUM_INGOT))
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineItems.SOLAR_PANEL_UNIT.get(), 2)
                .pattern("PDP")
                .pattern("RLR")
                .define('P', ChaosEngineItems.BASIC_MACHINE_PANEL.get())
                .define('D', Items.DAYLIGHT_DETECTOR)
                .define('R', Items.REDSTONE)
                .define('L', ChaosEngineItems.LOGIC_ASSEMBLY.get())
                .unlockedBy("has_aluminium_ingot", has(ChaosEngineItems.ALUMINIUM_INGOT))
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT)).save(recipeOutput);

        // TEMPORARY RECIPES
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineItems.ALUMINA_DUST.get(), 3)
                .pattern("WB")
                .pattern("BB")
                .define('W', Items.BONE_MEAL)
                .define('B', ChaosEngineItems.BAUXITE_DUST)
                .unlockedBy("has_bauxite_dust", has(ChaosEngineItems.BAUXITE_DUST)).save(recipeOutput);

        // Machines
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineBlocks.COMPACT_MACHINE_CHASSIS.get())
                .pattern("IPI")
                .pattern("P P")
                .pattern("IPI")
                .define('P', ChaosEngineItems.BASIC_MACHINE_PANEL.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_basic_machine_panel", has(ChaosEngineItems.BASIC_MACHINE_PANEL.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineBlocks.COMPACT_COAL_GENERATOR.get())
                .pattern("I I")
                .pattern("WXL")
                .pattern("IFI")
                .define('I', Items.IRON_INGOT)
                .define('W', Items.WATER_BUCKET)
                .define('X', ChaosEngineBlocks.COMPACT_MACHINE_CHASSIS.get())
                .define('L', ChaosEngineItems.LOGIC_ASSEMBLY.get())
                .define('F', Items.FURNACE)
                .unlockedBy("has_compact_machine_chassis", has(ChaosEngineBlocks.COMPACT_COAL_GENERATOR.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineBlocks.COMPACT_INDUCTION_FOUNDRY.get())
                .pattern("RCR")
                .pattern("LXL")
                .pattern("RFR")
                .define('R', Items.REDSTONE)
                .define('C', ChaosEngineItems.COPPER_COILS.get())
                .define('L', ChaosEngineItems.LOGIC_ASSEMBLY.get())
                .define('X', ChaosEngineBlocks.COMPACT_MACHINE_CHASSIS.get())
                .define('F', Items.FURNACE)
                .unlockedBy("has_compact_machine_chassis", has(ChaosEngineBlocks.COMPACT_MACHINE_CHASSIS.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineBlocks.COMPACT_PULVERIZER.get())
                .pattern("I I")
                .pattern("LXL")
                .pattern("RGR")
                .define('I', Items.IRON_INGOT)
                .define('L', ChaosEngineItems.LOGIC_ASSEMBLY.get())
                .define('X', ChaosEngineBlocks.COMPACT_MACHINE_CHASSIS.get())
                .define('R', Items.REDSTONE)
                .define('G', Items.GRINDSTONE)
                .unlockedBy("has_compact_machine_chassis", has(ChaosEngineBlocks.COMPACT_MACHINE_CHASSIS.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineBlocks.COMPACT_REFINERY.get())
                .pattern("III")
                .pattern("LXL")
                .pattern("RBR")
                .define('I', Items.IRON_INGOT)
                .define('L', ChaosEngineItems.LOGIC_ASSEMBLY.get())
                .define('X', ChaosEngineBlocks.COMPACT_MACHINE_CHASSIS.get())
                .define('R', Items.REDSTONE)
                .define('B', Items.BREWING_STAND)
                .unlockedBy("has_compact_machine_chassis", has(ChaosEngineBlocks.COMPACT_MACHINE_CHASSIS.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineBlocks.ATMOSPHERIC_CONDENSER.get())
                .pattern("CCC")
                .pattern("DXH")
                .pattern("RWR")
                .define('C', Items.COPPER_INGOT)
                .define('D', ChaosEngineItems.DEPLOYMENT_ASSEMBLY.get())
                .define('X', ChaosEngineBlocks.COMPACT_MACHINE_CHASSIS.get())
                .define('H', ChaosEngineItems.HEAT_PUMP_ASSEMBLY.get())
                .define('R', Items.REDSTONE)
                .define('W', Items.WATER_BUCKET)
                .unlockedBy("has_compact_machine_chassis", has(ChaosEngineBlocks.COMPACT_MACHINE_CHASSIS.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineBlocks.SUSPENSION_MIXER.get())
                .pattern("DXL")
                .pattern("RCR")
                .define('D', ChaosEngineItems.DEPLOYMENT_ASSEMBLY)
                .define('L', ChaosEngineItems.LOGIC_ASSEMBLY.get())
                .define('X', ChaosEngineBlocks.COMPACT_MACHINE_CHASSIS.get())
                .define('R', Items.REDSTONE)
                .define('C', Items.CAULDRON)
                .unlockedBy("has_compact_machine_chassis", has(ChaosEngineBlocks.COMPACT_MACHINE_CHASSIS.get())).save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ChaosEngineBlocks.DEPLOYABLE_SOLAR.get())
                .pattern("S S")
                .pattern("SDS")
                .pattern("RXR")
                .define('S', ChaosEngineItems.SOLAR_PANEL_UNIT.get())
                .define('D', ChaosEngineItems.DEPLOYMENT_ASSEMBLY)
                .define('R', Items.REDSTONE)
                .define('X', ChaosEngineBlocks.COMPACT_MACHINE_CHASSIS.get())
                .unlockedBy("has_compact_machine_chassis", has(ChaosEngineBlocks.COMPACT_MACHINE_CHASSIS.get())).save(recipeOutput);

        /* STAIRS */
        stairBuilder(ChaosEngineBlocks.BAUXITE_STAIRS.get(), Ingredient.of(ChaosEngineBlocks.BAUXITE.get())).group("bauxite")
                .unlockedBy("has_bauxite", has(ChaosEngineBlocks.BAUXITE.get())).save(recipeOutput);
        stairBuilder(ChaosEngineBlocks.POLISHED_BAUXITE_STAIRS.get(), Ingredient.of(ChaosEngineBlocks.POLISHED_BAUXITE.get())).group("bauxite")
                .unlockedBy("has_polished_bauxite", has(ChaosEngineBlocks.POLISHED_BAUXITE.get())).save(recipeOutput);
        stairBuilder(ChaosEngineBlocks.BAUXITE_BRICK_STAIRS.get(), Ingredient.of(ChaosEngineBlocks.BAUXITE_BRICKS.get())).group("bauxite")
                .unlockedBy("has_bauxite_bricks", has(ChaosEngineBlocks.BAUXITE_BRICKS.get())).save(recipeOutput);
        stairBuilder(ChaosEngineBlocks.BAUXITE_TILE_STAIRS.get(), Ingredient.of(ChaosEngineBlocks.BAUXITE_TILES.get())).group("bauxite")
                .unlockedBy("has_bauxite_tiles", has(ChaosEngineBlocks.BAUXITE_TILES.get())).save(recipeOutput);

        /* SLABS */
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_SLAB.get(), ChaosEngineBlocks.BAUXITE);
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.POLISHED_BAUXITE_SLAB.get(), ChaosEngineBlocks.POLISHED_BAUXITE);
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_BRICK_SLAB.get(), ChaosEngineBlocks.BAUXITE_BRICKS);
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILE_SLAB.get(), ChaosEngineBlocks.BAUXITE_TILES);

        /* WALLS */
        wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_WALL.get(), ChaosEngineBlocks.BAUXITE);
        wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.POLISHED_BAUXITE_WALL.get(), ChaosEngineBlocks.POLISHED_BAUXITE);
        wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_BRICK_WALL.get(), ChaosEngineBlocks.BAUXITE_BRICKS);
        wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILE_WALL.get(), ChaosEngineBlocks.BAUXITE_TILES);

        /* STONECUTTER */
        // (So many permutations...)
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_STAIRS.get(), ChaosEngineBlocks.BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_SLAB.get(), ChaosEngineBlocks.BAUXITE.get(), 2);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_WALL.get(), ChaosEngineBlocks.BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_BRICKS.get(), ChaosEngineBlocks.BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_BRICK_STAIRS.get(), ChaosEngineBlocks.BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_BRICK_SLAB.get(), ChaosEngineBlocks.BAUXITE.get(), 2);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_BRICK_WALL.get(), ChaosEngineBlocks.BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.POLISHED_BAUXITE.get(), ChaosEngineBlocks.BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.POLISHED_BAUXITE_STAIRS.get(), ChaosEngineBlocks.BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.POLISHED_BAUXITE_SLAB.get(), ChaosEngineBlocks.BAUXITE.get(), 2);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.POLISHED_BAUXITE_WALL.get(), ChaosEngineBlocks.BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILES.get(), ChaosEngineBlocks.BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILE_STAIRS.get(), ChaosEngineBlocks.BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILE_SLAB.get(), ChaosEngineBlocks.BAUXITE.get(), 2);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILE_WALL.get(), ChaosEngineBlocks.BAUXITE.get());

        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.POLISHED_BAUXITE_STAIRS.get(), ChaosEngineBlocks.POLISHED_BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.POLISHED_BAUXITE_SLAB.get(), ChaosEngineBlocks.POLISHED_BAUXITE.get(), 2);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.POLISHED_BAUXITE_WALL.get(), ChaosEngineBlocks.POLISHED_BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_BRICKS.get(), ChaosEngineBlocks.POLISHED_BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_BRICK_STAIRS.get(), ChaosEngineBlocks.POLISHED_BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_BRICK_SLAB.get(), ChaosEngineBlocks.POLISHED_BAUXITE.get(), 2);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_BRICK_WALL.get(), ChaosEngineBlocks.POLISHED_BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILES.get(), ChaosEngineBlocks.POLISHED_BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILE_STAIRS.get(), ChaosEngineBlocks.POLISHED_BAUXITE.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILE_SLAB.get(), ChaosEngineBlocks.POLISHED_BAUXITE.get(), 2);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILE_WALL.get(), ChaosEngineBlocks.POLISHED_BAUXITE.get());

        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_BRICK_STAIRS.get(), ChaosEngineBlocks.BAUXITE_BRICKS.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_BRICK_SLAB.get(), ChaosEngineBlocks.BAUXITE_BRICKS.get(), 2);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_BRICK_WALL.get(), ChaosEngineBlocks.BAUXITE_BRICKS.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILES.get(), ChaosEngineBlocks.BAUXITE_BRICKS.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILE_STAIRS.get(), ChaosEngineBlocks.BAUXITE_BRICKS.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILE_SLAB.get(), ChaosEngineBlocks.BAUXITE_BRICKS.get(), 2);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILE_WALL.get(), ChaosEngineBlocks.BAUXITE_BRICKS.get());

        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILE_STAIRS.get(), ChaosEngineBlocks.BAUXITE_TILES.get());
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILE_SLAB.get(), ChaosEngineBlocks.BAUXITE_TILES.get(), 2);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ChaosEngineBlocks.BAUXITE_TILE_WALL.get(), ChaosEngineBlocks.BAUXITE_TILES.get());

        /* SHAPELESS RECIPES */
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ChaosEngineItems.IRON_DUST.get(), 9)
                .requires(ChaosEngineBlocks.IRON_DUST_BLOCK.get())
                .unlockedBy("has_iron_dust_block", has(ChaosEngineBlocks.IRON_DUST_BLOCK.get())).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ChaosEngineItems.GOLD_DUST.get(), 9)
                .requires(ChaosEngineBlocks.GOLD_DUST_BLOCK.get())
                .unlockedBy("has_gold_dust_block", has(ChaosEngineBlocks.GOLD_DUST_BLOCK.get())).save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ChaosEngineItems.COPPER_DUST.get(), 9)
                .requires(ChaosEngineBlocks.COPPER_DUST_BLOCK.get())
                .unlockedBy("has_copper_dust_block", has(ChaosEngineBlocks.COPPER_DUST_BLOCK.get())).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, Items.BLUE_DYE, 1)
                .requires(ChaosEngineItems.LAPIS_LAZULI_DUST.get())
                .unlockedBy("has_lapis_lazuli_dust", has(ChaosEngineItems.LAPIS_LAZULI_DUST.get())).save(recipeOutput);

        /* ORE SMELTING */
        oreSmeltingSingle(recipeOutput, ChaosEngineItems.IRON_DUST.get(), RecipeCategory.MISC, Items.IRON_INGOT,
                0.7f, 180, "iron_ingot");
        oreSmeltingSingle(recipeOutput, ChaosEngineItems.GOLD_DUST.get(), RecipeCategory.MISC, Items.GOLD_INGOT,
                0.7f, 180, "gold_ingot");
        oreSmeltingSingle(recipeOutput, ChaosEngineItems.COPPER_DUST.get(), RecipeCategory.MISC, Items.COPPER_INGOT,
                0.7f, 180, "copper_ingot");
        oreSmeltingSingle(recipeOutput, ChaosEngineItems.BAUXITE_DUST.get(), RecipeCategory.MISC, ChaosEngineItems.ALUMINIUM_INGOT,
                0.7f, 480, "aluminium_ingot");
        oreSmeltingSingle(recipeOutput, ChaosEngineItems.ALUMINA_BRONZE_DUST.get(), RecipeCategory.MISC, ChaosEngineItems.ALUMINA_BRONZE_INGOT,
                0.7f, 180, "alumina_bronze_ingot");

        /* ORE BLASTING */
        oreBlastingSingle(recipeOutput, ChaosEngineItems.IRON_DUST.get(), RecipeCategory.MISC, Items.IRON_INGOT,
                0.7f, 90, "iron_ingot");
        oreBlastingSingle(recipeOutput, ChaosEngineItems.GOLD_DUST.get(), RecipeCategory.MISC, Items.GOLD_INGOT,
                0.7f, 90, "gold_ingot");
        oreBlastingSingle(recipeOutput, ChaosEngineItems.COPPER_DUST.get(), RecipeCategory.MISC, Items.COPPER_INGOT,
                0.7f, 90, "copper_ingot");
        oreBlastingSingle(recipeOutput, ChaosEngineItems.BAUXITE_DUST.get(), RecipeCategory.MISC, ChaosEngineItems.ALUMINIUM_INGOT,
                0.7f, 240, "aluminium_ingot");
        oreBlastingSingle(recipeOutput, ChaosEngineItems.ALUMINA_BRONZE_DUST.get(), RecipeCategory.MISC, ChaosEngineItems.ALUMINA_BRONZE_INGOT,
                0.7f, 90, "alumina_bronze_ingot");
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
