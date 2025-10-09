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

@EventBusSubscriber(modid = TheChaosEngine.MOD_ID)
public class ChaosEngineBusEvents {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        /* Energy Capabilities */
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ChaosEngineBlockEntities.COMPACT_COAL_GENERATOR_BE.get(),
                (blockEntity, side) -> {
            Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            return (side == facing.getClockWise()) ? blockEntity.getEnergyStorage(side) : null;
        });
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ChaosEngineBlockEntities.COMPACT_PULVERIZER_BE.get(),
                (blockEntity, side) -> {
            Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            boolean isValidSide = side == facing.getOpposite() || side == Direction.DOWN || side == facing;
            return isValidSide ? blockEntity.getEnergyStorage(side) : null;
        });
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ChaosEngineBlockEntities.COMPACT_INDUCTION_FOUNDRY_BE.get(),
                (blockEntity, side) -> {
            Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            boolean isValidSide = side == facing.getOpposite() || side == Direction.DOWN || side == facing;
            return isValidSide ? blockEntity.getEnergyStorage(side) : null;
        });
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ChaosEngineBlockEntities.ATMOSPHERIC_CONDENSER_BE.get(),
                (blockEntity, side) -> {
            Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            return (side == facing.getOpposite() || side == Direction.DOWN) ? blockEntity.getEnergyStorage(side) : null;
        });
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ChaosEngineBlockEntities.SUSPENSION_MIXER_BE.get(),
                (blockEntity, side) -> {
            Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            return (side == Direction.DOWN || side == facing) ? blockEntity.getEnergyStorage(side) : null;
        });
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ChaosEngineBlockEntities.DEPLOYABLE_SOLAR_BE.get(),
                (blockEntity, side) -> {
            Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            return (side == Direction.DOWN || side == facing.getOpposite()) ? blockEntity.getEnergyStorage(side) : null;
        });

        /* Fluid Capabilities */

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ChaosEngineBlockEntities.ATMOSPHERIC_CONDENSER_BE.get(),
                (blockEntity, side) -> {
            Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            return (side == facing.getClockWise() || side == facing.getCounterClockWise()) ? blockEntity.getTank(side): null;
                });
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

        /* Item Capabilities */
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
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ChaosEngineBlockEntities.SUSPENSION_MIXER_BE.get(),
                SidedInvWrapper::new);
    }
}
