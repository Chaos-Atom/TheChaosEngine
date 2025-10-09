package net.chaosatom.thechaosengine.block.entity;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.ChaosEngineBlocks;
import net.chaosatom.thechaosengine.block.entity.custom.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ChaosEngineBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, TheChaosEngine.MOD_ID);

    public static final Supplier<BlockEntityType<CompactCoalGeneratorBlockEntity>> COMPACT_COAL_GENERATOR_BE =
            BLOCK_ENTITIES.register("coal_generator_be", () -> BlockEntityType.Builder.of(
                    CompactCoalGeneratorBlockEntity::new, ChaosEngineBlocks.COMPACT_COAL_GENERATOR.get()).build(null));

    public static final Supplier<BlockEntityType<CompactPulverizerBlockEntity>> COMPACT_PULVERIZER_BE =
            BLOCK_ENTITIES.register("compact_pulverizer_be", () -> BlockEntityType.Builder.of(
                    CompactPulverizerBlockEntity::new, ChaosEngineBlocks.COMPACT_PULVERIZER.get()).build(null));

    public static final Supplier<BlockEntityType<CompactInductionFoundryBlockEntity>> COMPACT_INDUCTION_FOUNDRY_BE =
            BLOCK_ENTITIES.register("compact_induction_foundry_be", () -> BlockEntityType.Builder.of(
                    CompactInductionFoundryBlockEntity::new, ChaosEngineBlocks.COMPACT_INDUCTION_FOUNDRY.get()).build(null));

    public static final Supplier<BlockEntityType<AtmosphericCondenserBlockEntity>> ATMOSPHERIC_CONDENSER_BE =
            BLOCK_ENTITIES.register("atmospheric_condenesr_be", () -> BlockEntityType.Builder.of(
                    AtmosphericCondenserBlockEntity::new, ChaosEngineBlocks.ATMOSPHERIC_CONDENSER.get()).build(null));

    public static final Supplier<BlockEntityType<SuspensionMixerBlockEntity>> SUSPENSION_MIXER_BE =
            BLOCK_ENTITIES.register("suspension_mixer_be", () -> BlockEntityType.Builder.of(
                    SuspensionMixerBlockEntity::new, ChaosEngineBlocks.SUSPENSION_MIXER.get()).build(null));

    public static final Supplier<BlockEntityType<DeployableSolarBlockEntity>> DEPLOYABLE_SOLAR_BE =
            BLOCK_ENTITIES.register("deployable_solar_be", () -> BlockEntityType.Builder.of(
                    DeployableSolarBlockEntity::new, ChaosEngineBlocks.DEPLOYABLE_SOLAR.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
