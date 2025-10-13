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

public record RefineryRecipe(Ingredient itemIngredient, FluidStack fluidIngredient, ItemStack output, int processTime, int energy)
        implements Recipe<SingleItemRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(itemIngredient);
        return list;
    }

    @Override
    public boolean matches(SingleItemRecipeInput recipeInput, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        return itemIngredient.test(recipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(SingleItemRecipeInput recipeInput, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ChaosEngineRecipes.REFINERY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ChaosEngineRecipes.REFINERY_TYPE.get();
    }

    public interface Factory<T extends RefineryRecipe> {
        T create(String string, CookingBookCategory bookCategory, Ingredient itemIngredient, FluidStack fluidIngredient,
                 ItemStack result, int processTime, int energy);
    }

    public static class Serializer implements RecipeSerializer<RefineryRecipe> {
        private static final MapCodec<RefineryRecipe> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("item_ingredient").forGetter(RefineryRecipe::itemIngredient),
                        FluidStack.CODEC.fieldOf("fluid_ingredient").forGetter(RefineryRecipe::fluidIngredient),
                        ItemStack.CODEC.fieldOf("result").forGetter(RefineryRecipe::output),
                        Codec.INT.fieldOf("process_time").forGetter(RefineryRecipe::processTime),
                        Codec.INT.fieldOf("energy").forGetter(RefineryRecipe::energy))
                        .apply(inst, RefineryRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, RefineryRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, RefineryRecipe::itemIngredient,
                        FluidStack.STREAM_CODEC, RefineryRecipe::fluidIngredient,
                        ItemStack.STREAM_CODEC, RefineryRecipe::output,
                        ByteBufCodecs.VAR_INT, RefineryRecipe::processTime,
                        ByteBufCodecs.VAR_INT, RefineryRecipe::energy,
                        RefineryRecipe::new);

        @Override
        public MapCodec<RefineryRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RefineryRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
