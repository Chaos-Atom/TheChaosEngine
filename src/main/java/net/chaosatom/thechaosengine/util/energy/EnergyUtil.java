package net.chaosatom.thechaosengine.util.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

// 100% of this comes from Kaupenjoe's tutorials, huge thanks to him.
public class EnergyUtil {
    public static boolean moveEnergy(BlockPos giverPos, BlockPos receiverPos, int amount, Level level) {
        IEnergyStorage giverStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, giverPos, null);
        IEnergyStorage receiverStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, receiverPos, null);

        if (canReceiverAcceptMoreEnergy(giverStorage, amount)) {
            return false;
        }

        if (canGiverSendMoreEnergy(receiverStorage)) {
            return false;
        }

        int maxAmountToReceive = receiverStorage.receiveEnergy(amount, true);

        int extractedEnergy = giverStorage.extractEnergy(maxAmountToReceive, false);
        receiverStorage.receiveEnergy(extractedEnergy, false);

        return true;
    }

    private static boolean canReceiverAcceptMoreEnergy(IEnergyStorage giverStorage, int amount) {
        // Checks if more energy can be sent from giver (False if receiver is full)
        return giverStorage.getEnergyStored() <= 0 || giverStorage.getEnergyStored() < amount || !giverStorage.canExtract();
    }

    private static boolean canGiverSendMoreEnergy(IEnergyStorage receiverStorage) {
        // Checks if giver has energy remaining to send (False if giver is empty)
        return receiverStorage.getEnergyStored() >= receiverStorage.getMaxEnergyStored() || !receiverStorage.canReceive();
    }

    public static  boolean doesBlockHaveEnergyStorage(BlockPos positionToCheck, Level level) {
        return level.getBlockEntity(positionToCheck) != null
                && level.getCapability(Capabilities.EnergyStorage.BLOCK, positionToCheck, null) != null;
    }
}
