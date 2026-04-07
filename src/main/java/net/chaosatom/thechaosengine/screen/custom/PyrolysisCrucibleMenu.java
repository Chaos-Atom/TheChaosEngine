package net.chaosatom.thechaosengine.screen.custom;

import net.chaosatom.thechaosengine.block.ChaosEngineBlocks;
import net.chaosatom.thechaosengine.block.entity.custom.PyrolysisCrucibleBlockEntity;
import net.chaosatom.thechaosengine.screen.ChaosEngineMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class PyrolysisCrucibleMenu extends AbstractContainerMenu {
    public final PyrolysisCrucibleBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public PyrolysisCrucibleMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public PyrolysisCrucibleMenu(int containerId, Inventory inventory, BlockEntity entity, ContainerData data) {
        super(ChaosEngineMenuTypes.PYROLYSIS_CRUCIBLE_MENU.get(), containerId);
        this.blockEntity = (PyrolysisCrucibleBlockEntity) entity;
        this.level = inventory.player.level();
        this.data = data;

        addDataSlots(data);
        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 0, 44,56));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 1, 116,57));
    }

    public boolean isCoking() { // Is "coking" a real word?
        return data.get(0) > 0;
    }

    public int getScaledArrowProgress(int width) {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int heatBonus = this.data.get(2);

        int updatedMaxProgress = maxProgress - (heatBonus * 15);

        return maxProgress != 0 && progress != 0 ? (progress * width) / updatedMaxProgress : 0;
    }

    public int getScaledHeatBonus(int height) {
        int heatBonus = this.data.get(2);
        int maxHeatBonus = this.data.get(3);

        return maxHeatBonus != 0 && heatBonus != 0 ? (heatBonus * height) / maxHeatBonus : 0;
    }

    public ContainerData getData() {
        return this.data;
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 2;  // must be the number of slots you have!
    @Override
    public @NotNull ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ChaosEngineBlocks.PYROLYSIS_CRUCIBLE.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; i++) {
            for (int l = 0; l < 9; l++) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 122 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerHotBar) {
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerHotBar, i, 8 + i * 18, 180));
        }
    }
}