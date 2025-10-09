package net.chaosatom.thechaosengine.screen;

import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.screen.custom.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ChaosEngineMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, TheChaosEngine.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<CompactCoalGeneratorMenu>> COMPACT_COAL_GENERATOR_MENU =
            registerMenuType("coal_generator_menu", CompactCoalGeneratorMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<CompactPulverizerMenu>> COMPACT_PULVERIZER_MENU =
            registerMenuType("compact_pulverizer_menu", CompactPulverizerMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<CompactInductionFoundryMenu>> COMPACT_INDUCTION_FOUNDRY_MENU =
            registerMenuType("compact_induction_foundry_menu", CompactInductionFoundryMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<AtmosphericCondenserMenu>> ATMOSPHERIC_CONDENSER_MENU =
            registerMenuType("atmospheric_condenser_menu", AtmosphericCondenserMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<SuspensionMixerMenu>> SUSPENSION_MIXER_MENU =
            registerMenuType("suspension_mixer_menu", SuspensionMixerMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<DeployableSolarMenu>> DEPLOYABLE_SOLAR_MENU =
            registerMenuType("deployable_solar_menu", DeployableSolarMenu::new);

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>,
            MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
