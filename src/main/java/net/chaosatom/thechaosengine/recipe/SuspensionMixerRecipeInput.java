package net.chaosatom.thechaosengine.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public record SuspensionMixerRecipeInput(ItemStack itemInput, FluidStack fluidInput) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return itemInput;
    }

//    public FluidStack getFluid(int index) {
//        return fluidInput;
//    }

    @Override
    public int size() {
        return 2;
    }
}
