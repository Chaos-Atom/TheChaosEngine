package net.chaosatom.thechaosengine;

import net.chaosatom.thechaosengine.block.entity.ModBlockEntities;
import net.chaosatom.thechaosengine.block.entity.custom.CompactCoalGeneratorBlockEntity;
import net.chaosatom.thechaosengine.block.entity.custom.CompactPulverizerBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = TheChaosEngine.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModBusEvents {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.COMPACT_COAL_GENERATOR_BE.get(),
                CompactCoalGeneratorBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.COMPACT_PULVERIZER_BE.get(),
                CompactPulverizerBlockEntity::getEnergyStorage);

        // Checks that the input side of the generator is a valid item pusher, accepts items from that side only
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.COMPACT_COAL_GENERATOR_BE.get(),
                (blockEntity, side) -> {
                    Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
                    Direction leftSide = facing.getCounterClockWise();

                    if (side == leftSide) {
                        return blockEntity.itemHandler;
                    }
                    return null;
                });
    }
}
