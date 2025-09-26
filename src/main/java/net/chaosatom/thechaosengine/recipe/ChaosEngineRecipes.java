package net.chaosatom.thechaosengine.recipe;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ChaosEngineRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, TheChaosEngine.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, TheChaosEngine.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PulverizerRecipe>> PULVERIZER_SERIALIZER =
            SERIALIZERS.register("pulverizing", PulverizerRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<PulverizerRecipe>> PULVERIZER_TYPE =
            TYPES.register("pulverizing", () -> new RecipeType<PulverizerRecipe>() {
                @Override
                public String toString() {
                    return "pulverizing";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InductionFoundryRecipe>> INDUCTION_FOUNDRY_SERIALIZER =
            SERIALIZERS.register("induction_smelting", InductionFoundryRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<InductionFoundryRecipe>> INDUCTION_FOUNDRY_TYPE =
            TYPES.register("induction_smelting", () -> new RecipeType<InductionFoundryRecipe>() {
                @Override
                public String toString() {
                    return "induction_smelting";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SuspensionMixerRecipe>> SUSPENSION_MIXER_SERIALIZER =
            SERIALIZERS.register("suspension_mixing", SuspensionMixerRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<SuspensionMixerRecipe>> SUSPENSION_MIXER_TYPE =
            TYPES.register("suspension_mixing", () -> new RecipeType<SuspensionMixerRecipe>() {
                @Override
                public String toString() {
                    return "suspension_mixing";
                }
            });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
