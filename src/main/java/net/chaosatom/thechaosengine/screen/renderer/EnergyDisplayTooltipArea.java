package net.chaosatom.thechaosengine.screen.renderer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

/*
 *  BluSunrize
 *  Copyright (c) 2021
 *
 *  This code is licensed under "Blu's License of Common Sense"
 *  https://github.com/BluSunrize/ImmersiveEngineering/blob/1.19.2/LICENSE
 *
 *  Slightly Modified Version by: Kaupenjoe
 */
public class EnergyDisplayTooltipArea {
    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;
    private final IEnergyStorage energy;

    public EnergyDisplayTooltipArea(int xMin, int yMin, IEnergyStorage energy)  {
        this(xMin, yMin, energy,8,64);
    }

    public EnergyDisplayTooltipArea(int xMin, int yMin, IEnergyStorage energy, int width, int height)  {
        xPos = xMin;
        yPos = yMin;
        this.width = width;
        this.height = height;
        this.energy = energy;
    }

    public List<Component> getTooltips() {
        return List.of(Component.literal(energy.getEnergyStored()+" / "+energy.getMaxEnergyStored()+" FE"));
    }

    public void render(GuiGraphics guiGraphics) {
        int stored = (int)(height * (energy.getEnergyStored() / (float)energy.getMaxEnergyStored()));
        /* For future reference because coding is scary still
        "0x" means the int is using hexadecimal 0-15 as 0-9 & a-f
        using ARGB setup with ff meaning 255 (max) in alpha channel (15*16^1) + (15*16^0)
        Example: 0xffb51500 is A:ff, R:b5, G:15, B:00 or A:255, R:181, G:15, B:00
        Placeholder for original values: (0xffb51500, 0xff600b00)
         */
        guiGraphics.fillGradient(xPos,yPos + (height - stored),xPos + width,
                yPos + height,0xff13cea9, 0xff156f5d);
    }
}