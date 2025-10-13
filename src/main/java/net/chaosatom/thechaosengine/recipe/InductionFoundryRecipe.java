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
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record InductionFoundryRecipe(Ingredient inputItem, ItemStack output, int processTime, int energy) implements Recipe<SingleItemRecipeInput> {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(SingleItemRecipeInput recipeInput, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        return inputItem.test(recipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(SingleItemRecipeInput recipeInput, HolderLookup.Provider provider) {
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
        return ChaosEngineRecipes.INDUCTION_FOUNDRY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ChaosEngineRecipes.INDUCTION_FOUNDRY_TYPE.get();
    }

    public interface Factory<T extends InductionFoundryRecipe> {
        T create(String string, CookingBookCategory bookCategory, Ingredient ingredient, ItemStack result, int processTime, int energy);
    }

    public static class Serializer implements RecipeSerializer<InductionFoundryRecipe> {
        private static final MapCodec<InductionFoundryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(InductionFoundryRecipe::inputItem),
                        ItemStack.CODEC.fieldOf("result").forGetter(InductionFoundryRecipe::output),
                        Codec.INT.fieldOf("process_time").forGetter(InductionFoundryRecipe::processTime),
                        Codec.INT.fieldOf("energy").forGetter(InductionFoundryRecipe::energy))
                        .apply(instance, InductionFoundryRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, InductionFoundryRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, InductionFoundryRecipe::inputItem,
                        ItemStack.STREAM_CODEC, InductionFoundryRecipe::output,
                        ByteBufCodecs.VAR_INT, InductionFoundryRecipe::processTime,
                        ByteBufCodecs.VAR_INT, InductionFoundryRecipe::energy,
                        InductionFoundryRecipe::new);

        @Override
        public MapCodec<InductionFoundryRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, InductionFoundryRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
