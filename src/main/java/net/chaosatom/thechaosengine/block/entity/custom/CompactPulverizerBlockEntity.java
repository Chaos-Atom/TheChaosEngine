package net.chaosatom.thechaosengine.block.entity.custom;

import net.chaosatom.thechaosengine.block.entity.ModBlockEntities;
import net.chaosatom.thechaosengine.block.entity.energy.ModEnergyStorage;
import net.chaosatom.thechaosengine.recipe.ModRecipes;
import net.chaosatom.thechaosengine.recipe.PulverizerRecipe;
import net.chaosatom.thechaosengine.recipe.PulverizerRecipeInput;
import net.chaosatom.thechaosengine.screen.custom.CompactPulverizerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CompactPulverizerBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    // Makes it easier to tell what each slot index represents
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    private final ContainerData data;
    private int progress = 0;
    private int maxProgress = 65;
    private final int DEFAULT_MAX_PROGRESS = 65;

    private static final int ENERGY_CRAFT_AMOUNT = 1; // amount of energy per Tick to craft

    private final ModEnergyStorage ENERGY_STORAGE = createEnergyStorage();
    private ModEnergyStorage createEnergyStorage() {
        return new ModEnergyStorage(64000, 320) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                assert getLevel() != null;
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        };
    }

    public CompactPulverizerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.COMPACT_PULVERIZER_BE.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> CompactPulverizerBlockEntity.this.progress;
                    case 1 -> CompactPulverizerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: CompactPulverizerBlockEntity.this.progress = value;
                    case 1: CompactPulverizerBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return this.ENERGY_STORAGE;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    /* Main Processing Logic */

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        Optional<RecipeHolder<PulverizerRecipe>> recipe = getCurrentRecipe();
        boolean wasWorkingThisTick = false;

        if (recipe.isEmpty() || !isOutputSlotEmptyOrReceivable()) {
            resetProgress();
            setChanged(level, blockPos, blockState);
            return;
        }

        if (this.ENERGY_STORAGE.getEnergyStored() >= maxProgress && hasRecipe()) {
            wasWorkingThisTick = true;
            useEnergyForCrafting();
            this.progress++;
            setChanged(level, blockPos, blockState);

            if (hasCraftingFinished()) {
                craftItem();
                wasWorkingThisTick = false;
                resetProgress();
            }
        }

        boolean isLit = wasWorkingThisTick;
        if (blockState.getValue(BlockStateProperties.LIT) != isLit) {
            level.setBlock(blockPos, blockState.setValue(BlockStateProperties.LIT, isLit), 3);
        }
        pushOutputs();
    }

    private void useEnergyForCrafting() {
        Optional<RecipeHolder<PulverizerRecipe>> recipe = getCurrentRecipe();
        int specificEnergyCost = recipe.get().value().energy();
        this.ENERGY_STORAGE.extractEnergy(specificEnergyCost, false);
    }

    // Custom Crafting Logic via Helper methods

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = DEFAULT_MAX_PROGRESS;
    }

    private void craftItem() {
        Optional<RecipeHolder<PulverizerRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return;
        }
        ItemStack output = recipe.get().value().output();

        itemHandler.extractItem(INPUT_SLOT, 1, false);
        itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(output.getItem(),
                itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount()));
    }

    private boolean hasCraftingFinished() {
        Optional<RecipeHolder<PulverizerRecipe>> recipe = getCurrentRecipe();
        int specificProgress = recipe.get().value().processTime();
        return this.progress >= specificProgress;
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() < this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<PulverizerRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return false;
        }

        ItemStack output = recipe.get().value().output();
        this.maxProgress = recipe.get().value().processTime();

        return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemInputIntoOutputSlot(output) &&
                this.ENERGY_STORAGE.getEnergyStored() >= recipe.get().value().energy();
    }

    private boolean hasEnoughEnergyToCraft() {
        return this.ENERGY_STORAGE.getEnergyStored() >= ENERGY_CRAFT_AMOUNT * maxProgress;
    }

    private Optional<RecipeHolder<PulverizerRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.PULVERIZER_TYPE.get(), new PulverizerRecipeInput(itemHandler.getStackInSlot(INPUT_SLOT)), level);
    }

    private boolean canInsertItemInputIntoOutputSlot(ItemStack output) {
        return itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                itemHandler.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();

        return maxCount >= currentCount + count;
    }

    // General Block Entity Methods

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("blockentity.thechaosengine.compact_pulverizer");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new CompactPulverizerMenu(containerId, inventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inventory", itemHandler.serializeNBT(registries));

        tag.putInt("compact_pulverizer.progress", progress);
        tag.putInt("compact_pulverizer.max_progress", maxProgress);

        tag.putInt("compact_pulverizer.energy", ENERGY_STORAGE.getEnergyStored());

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));

        progress = tag.getInt("compact_pulverizer.progress");
        maxProgress = tag.getInt("compact_pulverizer.max_progress");

        ENERGY_STORAGE.setEnergy(tag.getInt("compact_pulverizer.energy"));

        super.loadAdditional(tag, registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    /* Sided Inventory via WorldlyContainers
    * This stuff really melted my brain at first...
    */

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{INPUT_SLOT, OUTPUT_SLOT};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slotIndex, ItemStack itemStack, @Nullable Direction direction) {
        Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (direction == facing) {
            return false;
        }
        return slotIndex == INPUT_SLOT;
    }

    @Override
    public boolean canTakeItemThroughFace(int slotIndex, ItemStack itemStack, Direction direction) {
        return slotIndex == OUTPUT_SLOT;
    }

    @Override
    public int getContainerSize() {
        return itemHandler.getSlots();
    }

    @Override
    public boolean isEmpty() {
        return this.itemHandler.getStackInSlot(INPUT_SLOT).isEmpty() && itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty();
    }

    @Override
    public ItemStack getItem(int slotIndex) {
        return itemHandler.getStackInSlot(slotIndex);
    }

    @Override
    public ItemStack removeItem(int slotIndex, int count) {
        return this.itemHandler.extractItem(slotIndex, count, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slotIndex) {
        ItemStack stack = itemHandler.getStackInSlot(slotIndex);
        itemHandler.setStackInSlot(slotIndex, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slotIndex, ItemStack itemStack) {
        itemHandler.setStackInSlot(slotIndex, itemStack);
    }

    @Override
    public boolean stillValid(Player player) {
        // Checks if players are within a certain distance of interacting inventories
        return player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        // Empties output slot of items during extraction
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
    /* Additional Sided Inventory Logic */

    private void pushOutputs() {
        // Ends method call if there is no more items to push
        ItemStack outputStack = itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (outputStack.isEmpty()) {
            return;
        }

        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction[] outputDirections = { Direction.DOWN, facing.getClockWise() }; // Bottom & Right side of block

        // For each output side, check if neighbor is a block entity
        for (Direction direction : outputDirections) {
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
            if (neighbor == null) {
                continue; // Continue search if there's no block entity
            }

            // Obtains the ItemHandler Capability of neighboring blocks
            IItemHandler neighborHandler = level.getCapability(Capabilities.ItemHandler.BLOCK,
                    worldPosition.relative(direction),
                    direction.getOpposite());

            if (neighborHandler != null) {
                // If current checked block has ItemHandler Capability...
                // First check through ItemHandlerHelper that the ItemStack from our output can be inserted
                ItemStack remainder = ItemHandlerHelper.insertItem(neighborHandler, outputStack, false);

                // Obtains the remainder of what cannot be inserted
                // // In that case, the output slot is updated to match the remainder (as remaining items to be pushed)
                if (remainder.getCount() < outputStack.getCount()) {
                    itemHandler.setStackInSlot(OUTPUT_SLOT, remainder);
                    setChanged();
                    return; // Stops after item is successfully pushed to neighbor block
                }
            }
        }
    }
}
