package net.chaosatom.thechaosengine;

import net.chaosatom.thechaosengine.block.entity.ModBlockEntities;
import net.chaosatom.thechaosengine.block.entity.custom.CompactCoalGeneratorBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = TheChaosEngine.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModBusEvents {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.COAL_GENERATOR_BE.get(),
                CompactCoalGeneratorBlockEntity::getEnergyStorage);
    }
}
