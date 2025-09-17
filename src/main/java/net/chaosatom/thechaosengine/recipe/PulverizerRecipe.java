package net.chaosatom.thechaosengine.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record PulverizerRecipe(Ingredient inputItem, ItemStack output) implements Recipe<PulverizerRecipeInput> {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(PulverizerRecipeInput recipeInput, Level level) {
        if (level.isClientSide()) {
            return false;
        }

        // Checks that the input item from the JSON file (inputItem) matches item in the input slot of the pulverizer (slot index 0)
        return  inputItem.test(recipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(PulverizerRecipeInput recipeInput, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.PULVERIZER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.PULVERIZER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<PulverizerRecipe> {
        // Basically a way to serialize and deserialize data from different formats to/from JSON files
        // Within .fieldOf(String name), the string is the name that is within the JSON file
        private static final MapCodec<PulverizerRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> // Given an instance...
                inst.group( // Defines the fields within the instance
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(PulverizerRecipe::inputItem), // First field, an ItemStack as the ingredient...thing?
                ItemStack.CODEC.fieldOf("result").forGetter(PulverizerRecipe::output)) // Second field, an ItemStack as an output item
                .apply(inst, PulverizerRecipe::new)); // Defines how to create the object

        // Serializes and deserializes data into packets to send to server. Enables client-server communication
        public static final StreamCodec<RegistryFriendlyByteBuf, PulverizerRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, PulverizerRecipe::inputItem,
                        ItemStack.STREAM_CODEC, PulverizerRecipe::output,
                        PulverizerRecipe::new);

        @Override
        public MapCodec<PulverizerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PulverizerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
