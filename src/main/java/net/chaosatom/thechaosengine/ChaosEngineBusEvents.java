package net.chaosatom.thechaosengine;

import net.chaosatom.thechaosengine.block.entity.ChaosEngineBlockEntities;
import net.chaosatom.thechaosengine.block.entity.custom.*;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

@EventBusSubscriber(modid = TheChaosEngine.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ChaosEngineBusEvents {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ChaosEngineBlockEntities.COMPACT_COAL_GENERATOR_BE.get(),
                CompactCoalGeneratorBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ChaosEngineBlockEntities.COMPACT_PULVERIZER_BE.get(),
                CompactPulverizerBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ChaosEngineBlockEntities.COMPACT_INDUCTION_FOUNDRY_BE.get(),
                CompactInductionFoundryBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ChaosEngineBlockEntities.ATMOSPHERIC_CONDENSER_BE.get(),
                AtmosphericCondenserBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ChaosEngineBlockEntities.SUSPENSION_MIXER_BE.get(),
                SuspensionMixerBlockEntity::getEnergyStorage);

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ChaosEngineBlockEntities.ATMOSPHERIC_CONDENSER_BE.get(),
                AtmosphericCondenserBlockEntity::getTank);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ChaosEngineBlockEntities.SUSPENSION_MIXER_BE.get(),
                (blockEntity, side) -> {
            Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            Direction inputSide = facing.getCounterClockWise();
            Direction outputSide = facing.getClockWise();

            if (side == inputSide) {
                return blockEntity.getInputTank(side);
            }

            if (side == outputSide) {
                return blockEntity.getOutputTank(side);
            }
            return null;
        });

        /* Checks that the input side of the generator is a valid item pusher, accepts items from that side only
        * I am unsure if this is okay to put this logic in here...
        */
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ChaosEngineBlockEntities.COMPACT_COAL_GENERATOR_BE.get(),
                (blockEntity, side) -> {
                    Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
                    Direction leftSide = facing.getCounterClockWise();

                    if (side == leftSide) {
                        return blockEntity.itemHandler;
                    }
                    return null;
                });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ChaosEngineBlockEntities.COMPACT_PULVERIZER_BE.get(),
                SidedInvWrapper::new);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ChaosEngineBlockEntities.COMPACT_INDUCTION_FOUNDRY_BE.get(),
                SidedInvWrapper::new);
    }
}
