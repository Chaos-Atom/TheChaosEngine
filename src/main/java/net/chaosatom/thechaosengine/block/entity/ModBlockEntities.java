package net.chaosatom.thechaosengine.block.entity;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.ModBlocks;
import net.chaosatom.thechaosengine.block.entity.custom.CompactCoalGeneratorBlockEntity;
import net.chaosatom.thechaosengine.block.entity.custom.CompactInductionFoundryBlockEntity;
import net.chaosatom.thechaosengine.block.entity.custom.CompactPulverizerBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, TheChaosEngine.MOD_ID);

    public static final Supplier<BlockEntityType<CompactCoalGeneratorBlockEntity>> COMPACT_COAL_GENERATOR_BE =
            BLOCK_ENTITIES.register("coal_generator_be", () -> BlockEntityType.Builder.of(
                    CompactCoalGeneratorBlockEntity::new, ModBlocks.COMPACT_COAL_GENERATOR.get()).build(null));

    public static final Supplier<BlockEntityType<CompactPulverizerBlockEntity>> COMPACT_PULVERIZER_BE =
            BLOCK_ENTITIES.register("compact_pulverizer_be", () -> BlockEntityType.Builder.of(
                    CompactPulverizerBlockEntity::new, ModBlocks.COMPACT_PULVERIZER.get()).build(null));

    public static final Supplier<BlockEntityType<CompactInductionFoundryBlockEntity>> COMPACT_INDUCTION_FOUNDRY_BE =
            BLOCK_ENTITIES.register("compact_induction_foundry_be", () -> BlockEntityType.Builder.of(
                    CompactInductionFoundryBlockEntity::new, ModBlocks.COMPACT_INDUCTION_FOUNDRY.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
