package net.chaosatom.thechaosengine.screen.custom;

import net.chaosatom.thechaosengine.block.ChaosEngineBlocks;
import net.chaosatom.thechaosengine.block.entity.custom.CompactInductionFoundryBlockEntity;
import net.chaosatom.thechaosengine.screen.ChaosEngineMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CompactInductionFoundryMenu extends AbstractContainerMenu {
    public final CompactInductionFoundryBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    private final static double threshold = 0.4; // How much progress (%) till the second progress meter starts

    public CompactInductionFoundryMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public CompactInductionFoundryMenu(int containerId, Inventory inventory, BlockEntity entity, ContainerData data) {
        super(ChaosEngineMenuTypes.COMPACT_INDUCTION_FOUNDRY_MENU.get(), containerId);
        blockEntity = ((CompactInductionFoundryBlockEntity) entity);
        this.level = inventory.player.level();
        this.data = data;

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 0, 49, 37));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 1, 111, 37));

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    /* Basically, these two methods creates a two-stage progress bar, one fills up to the set threshold value (0.5 is 50%)
    *  Then the first progress meter stays filled while the second one begins filling up
     */
    public int getScaledCoilProgress(int coilPixelSize) {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);

        if (maxProgress == 0 || progress == 0) {
            return 0;
        }

        double progressPercent = (double)progress / (double)maxProgress;

        if (progressPercent <= threshold) {
            double coilPercent = progressPercent / threshold;
            return (int)(coilPercent * coilPixelSize);
        } else {
            return coilPixelSize;
        }
    }

    public int getScaledArrowProgress(int arrowPixelSize) {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        if (maxProgress == 0 || progress == 0) {
            return 0;
        }

        double progressPercent = (double)progress / (double)maxProgress;

        if (progressPercent > threshold) {
            double smeltingProgress = progressPercent - threshold;
            double smeltingTotal = 1.0 - threshold;
            double arrowPercent = smeltingProgress / smeltingTotal;
            return (int)(arrowPercent * arrowPixelSize);
        } else {
            return 0;
        }
    }


    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 2;  // must be the number of slots you have!
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
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
                player, ChaosEngineBlocks.COMPACT_INDUCTION_FOUNDRY.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; i++) {
            for (int l = 0; l < 9; l++) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerHotBar) {
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerHotBar, i, 8 + i * 18, 142));
        }
    }
}
