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
import net.neoforged.neoforge.fluids.FluidStack;

public record SuspensionMixerRecipe(
        Ingredient itemIngredient,
        FluidStack fluidIngredient,
        FluidStack output,
        int processTime,
        int energy) implements Recipe<SingleItemRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(itemIngredient);
        return list;
    }

    @Override
    public boolean matches(SingleItemRecipeInput input, Level level) {
        if(level.isClientSide()) {
            return false;
        }
        return itemIngredient.test(input.getItem(0)); // Simple logic can be used here, block entity will handle rest
    }

    @Override
    public ItemStack assemble(SingleItemRecipeInput recipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY; // No item is the result, only a FluidStack
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ChaosEngineRecipes.SUSPENSION_MIXER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ChaosEngineRecipes.SUSPENSION_MIXER_TYPE.get();
    }

    public interface Factory<T extends SuspensionMixerRecipe> {
        T create(String string, CookingBookCategory bookCategory, Ingredient itemIngredient, FluidStack fluidIngredient,
                 FluidStack result, int processTime, int energy);
    }

    public static class Serializer implements RecipeSerializer<SuspensionMixerRecipe> {
        private static final MapCodec<SuspensionMixerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("item_ingredient").forGetter(SuspensionMixerRecipe::itemIngredient),
                        FluidStack.CODEC.fieldOf("fluid_ingredient").forGetter(SuspensionMixerRecipe::fluidIngredient),
                        FluidStack.CODEC.fieldOf("result").forGetter(SuspensionMixerRecipe::output),
                        Codec.INT.fieldOf("process_time").forGetter(SuspensionMixerRecipe::processTime),
                        Codec.INT.fieldOf("energy").forGetter(SuspensionMixerRecipe::energy))
                        .apply(instance, SuspensionMixerRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, SuspensionMixerRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, SuspensionMixerRecipe::itemIngredient,
                        FluidStack.STREAM_CODEC, SuspensionMixerRecipe::fluidIngredient,
                        FluidStack.STREAM_CODEC, SuspensionMixerRecipe::output,
                        ByteBufCodecs.VAR_INT, SuspensionMixerRecipe::processTime,
                        ByteBufCodecs.VAR_INT, SuspensionMixerRecipe::energy,
                        SuspensionMixerRecipe::new);

        @Override
        public MapCodec<SuspensionMixerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SuspensionMixerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
