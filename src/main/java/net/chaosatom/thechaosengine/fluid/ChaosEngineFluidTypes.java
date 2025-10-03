package net.chaosatom.thechaosengine.fluid;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class ChaosEngineFluidTypes {
    public static final ResourceLocation SUSPENSION_FLUID_STILL = ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID,
            "block/suspension_fluid_still");
    public static final ResourceLocation SUSPENSION_FLUID_FLOWING = ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID,
            "block/suspension_fluid_flow");
    public static final ResourceLocation SUSPENSION_FLUID_OVERLAY = ResourceLocation.fromNamespaceAndPath(TheChaosEngine.MOD_ID,
            "block/suspension_fluid_overlay");

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, TheChaosEngine.MOD_ID);

    public static final Supplier<FluidType> LAPIS_SUSPENSION_FLUID_TYPE = registerFluidType("lapis_suspension_fluid",
            new BaseFluidType(SUSPENSION_FLUID_STILL, SUSPENSION_FLUID_FLOWING, SUSPENSION_FLUID_OVERLAY, 0xEF345EC3,
                    new Vector3f(52f / 255f, 94f / 255f, 195f / 255f), 1f , 4f,
                    FluidType.Properties.create().canSwim(true).canDrown(true).canPushEntity(true)
                            .supportsBoating(true).motionScale(0.014).lightLevel(2)));

    private static Supplier<FluidType> registerFluidType (String name, FluidType fluidType) {
        return FLUID_TYPES.register(name, () -> fluidType);
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}
