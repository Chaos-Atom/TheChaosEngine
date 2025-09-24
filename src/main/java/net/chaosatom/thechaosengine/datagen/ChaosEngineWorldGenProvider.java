package net.chaosatom.thechaosengine.datagen;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.worldgen.ChaosEngineBiomeModifiers;
import net.chaosatom.thechaosengine.worldgen.ChaosEngineConfiguredFeatures;
import net.chaosatom.thechaosengine.worldgen.ChaosEnginePlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ChaosEngineWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ChaosEngineConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ChaosEnginePlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ChaosEngineBiomeModifiers::bootstrap);

    public ChaosEngineWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(TheChaosEngine.MOD_ID));
    }
}