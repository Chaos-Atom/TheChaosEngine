package net.chaosatom.thechaosengine.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record FuelItemRecipe(Ingredient inputItem, int burnTime, int energyPerTick) implements Recipe<SingleItemRecipeInput> {
    @Override
    public boolean matches(SingleItemRecipeInput recipeInput, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        return inputItem.test(recipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(SingleItemRecipeInput recipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ChaosEngineRecipes.FUEL_GENERATOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ChaosEngineRecipes.FUEL_GENERATOR_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<FuelItemRecipe> {
        private static final MapCodec<FuelItemRecipe> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(FuelItemRecipe::inputItem),
                        Codec.INT.fieldOf("burnTime").forGetter(FuelItemRecipe::burnTime),
                        Codec.INT.fieldOf("energyPerTick").forGetter(FuelItemRecipe::energyPerTick))
                        .apply(inst, FuelItemRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, FuelItemRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, FuelItemRecipe::inputItem,
                        ByteBufCodecs.VAR_INT, FuelItemRecipe::burnTime,
                        ByteBufCodecs.VAR_INT, FuelItemRecipe::energyPerTick,
                        FuelItemRecipe::new);

        @Override
        public MapCodec<FuelItemRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FuelItemRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
