package net.chaosatom.thechaosengine.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record PyrolizingRecipe(Ingredient inputItem, ItemStack result, int processTime, int energy) implements Recipe<SingleItemRecipeInput> {
    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(SingleItemRecipeInput recipeInput, Level level) {
        if (level.isClientSide()) {
            return  false;
        }
        return inputItem.test(recipeInput.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(SingleItemRecipeInput recipeInput, HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        return result;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ChaosEngineRecipes.PYROLYSIS_CRUCIBLE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ChaosEngineRecipes.PYROLYSIS_CRUCIBLE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<PyrolizingRecipe> {
        private static final MapCodec<PyrolizingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(PyrolizingRecipe::inputItem),
                        ItemStack.CODEC.fieldOf("result").forGetter(PyrolizingRecipe::result),
                        Codec.INT.fieldOf("process_time").forGetter(PyrolizingRecipe::processTime),
                        Codec.INT.fieldOf("energy").forGetter(PyrolizingRecipe::energy))
                        .apply(inst, PyrolizingRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, PyrolizingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, PyrolizingRecipe::inputItem,
                        ItemStack.STREAM_CODEC, PyrolizingRecipe::result,
                        ByteBufCodecs.VAR_INT, PyrolizingRecipe::processTime,
                        ByteBufCodecs.VAR_INT, PyrolizingRecipe::energy,
                        PyrolizingRecipe::new
                );

        @Override
        public MapCodec<PyrolizingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PyrolizingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
