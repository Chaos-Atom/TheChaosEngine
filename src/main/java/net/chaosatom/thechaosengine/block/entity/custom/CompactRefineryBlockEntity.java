package net.chaosatom.thechaosengine.block.entity.custom;

import net.chaosatom.thechaosengine.block.entity.ChaosEngineBlockEntities;
import net.chaosatom.thechaosengine.fluid.ChaosEngineFluids;
import net.chaosatom.thechaosengine.recipe.ChaosEngineRecipes;
import net.chaosatom.thechaosengine.recipe.RefineryRecipe;
import net.chaosatom.thechaosengine.recipe.SuspensionMixerRecipe;
import net.chaosatom.thechaosengine.screen.custom.CompactRefineryMenu;
import net.chaosatom.thechaosengine.util.energy.EnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
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
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Ref;
import java.util.Optional;

public class CompactRefineryBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    private final ContainerData data;
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private int progress = 0;
    private int maxProgress;
    private static final int DEFAULT_MAX_PROGRESS = 75;
    private static final int FLUID_TRANSFER_AMOUNT = 500;

    public CompactRefineryBlockEntity(BlockPos pos, BlockState blockState) {
        super(ChaosEngineBlockEntities.COMPACT_REFINERY_BE.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> CompactRefineryBlockEntity.this.progress;
                    case 1 -> CompactRefineryBlockEntity.this.maxProgress;
                    case 2 -> CompactRefineryBlockEntity.this.FLUID_TANK.getFluidAmount();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: CompactRefineryBlockEntity.this.progress = value;
                    case 1: CompactRefineryBlockEntity.this.maxProgress = value;
                    case 2: break;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    /* Capabilities */
    public final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private final EnergyStorage ENERGY_STORAGE = createEnergyStorage();
    private EnergyStorage createEnergyStorage() {
        return new EnergyStorage(64000, 320) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                assert getLevel() != null;
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        };
    }

    public IEnergyStorage getEnergyStorage(Direction direction) {
        return this.ENERGY_STORAGE;
    }

    private final FluidTank FLUID_TANK = new FluidTank(8000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
        @Override
        // TODO: Find a way to dynamically allow certain fluids into input tank without hardcoding
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ChaosEngineFluids.LAPIS_SUSPENSION_SOURCE.get();
        }
    };

    public FluidStack getFluid() {
        return FLUID_TANK.getFluid();
    }

    public IFluidHandler getTank(Direction direction) {
        return FLUID_TANK;
    }

    /* Main Machine Logic */
    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        Optional<RecipeHolder<RefineryRecipe>> recipe = getCurrentRecipe();
        boolean wasWorkingThisTick;

        if (recipe.isEmpty() || !isOutputSlotEmptyOrReceivable()) {
            setChanged(level, blockPos, blockState);
            resetProgress();
            return;
        }

        if (hasResource() && hasRecipe()) {
            wasWorkingThisTick = true;
            useEnergyForCrafting();
            this.progress++;
            setChanged(level, blockPos, blockState);

            if (hasCraftingFinished(recipe.get().value())) {
                craftItem(recipe.get());
                resetProgress();
                wasWorkingThisTick = canStillWork();
            }
        } else {
            resetProgress();
            wasWorkingThisTick = false;
        }

        boolean isLit = wasWorkingThisTick;
        if (blockState.getValue(BlockStateProperties.LIT) != isLit) {
            level.setBlock(blockPos, blockState.setValue(BlockStateProperties.LIT, isLit), 3);
        }
    }

    /* Custom Crafting Logic via Helper Methods */
    private Optional<RecipeHolder<RefineryRecipe>> getCurrentRecipe() {
        assert this.level != null;
        return this.level.getRecipeManager()
                .getAllRecipesFor(ChaosEngineRecipes.REFINERY_TYPE.get()).stream().filter(
                        recipeHolder -> {
                            RefineryRecipe recipe = recipeHolder.value();
                            boolean itemMatches = recipe.itemIngredient().test(this.itemHandler.getStackInSlot(INPUT_SLOT));
                            boolean fluidMatches = this.FLUID_TANK.getFluid().is(recipe.fluidIngredient().getFluid()) &&
                                    this.FLUID_TANK.getFluidAmount() >= recipe.fluidIngredient().getAmount();
                            return itemMatches && fluidMatches;
                        }).findFirst();
    }

    private boolean hasResource() {
        Optional<RecipeHolder<RefineryRecipe>> recipe = getCurrentRecipe();
        boolean hasEnoughEnergy = this.ENERGY_STORAGE.getEnergyStored() >= recipe.get().value().energy();
        boolean hasEnoughFluid = this.FLUID_TANK.getFluidAmount() >= recipe.get().value().fluidIngredient().getAmount();

        return hasEnoughEnergy && hasEnoughFluid;
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<RefineryRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return false;
        }

        ItemStack output = recipe.get().value().output();
        this.maxProgress = recipe.get().value().processTime();

        return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemInputIntoOutputSlot(output) &&
                this.ENERGY_STORAGE.getEnergyStored() >= recipe.get().value().energy();
    }

    private void craftItem(RecipeHolder<RefineryRecipe> recipeHolder) {
        RefineryRecipe recipe = recipeHolder.value();

        this.itemHandler.extractItem(INPUT_SLOT, 1, false);
        this.FLUID_TANK.drain(recipe.fluidIngredient().getAmount(), IFluidHandler.FluidAction.EXECUTE);

        ItemStack output = recipe.output();
        this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(output.getItem(),
                itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount()));
    }

    private void useEnergyForCrafting() {
        Optional<RecipeHolder<RefineryRecipe>> recipe = getCurrentRecipe();
        int specificEnergyCost = recipe.get().value().energy();
        this.ENERGY_STORAGE.extractEnergy(specificEnergyCost, false);
    }

    private boolean hasCraftingFinished(RefineryRecipe recipe) {
        int specificProgress = recipe.processTime();
        return this.progress >= specificProgress;
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() < this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean canInsertItemInputIntoOutputSlot(ItemStack output) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                itemHandler.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();

        return maxCount >= currentCount + currentCount;
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = DEFAULT_MAX_PROGRESS;
    }

    private boolean canStillWork() {
        return itemHandler.getStackInSlot(INPUT_SLOT).getCount() > 0 && hasRecipe();
    }

    /* General Block Entity Methods */
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("compact_refinery.inventory", itemHandler.serializeNBT(registries));
        tag.putInt("compact_refinery.energy", ENERGY_STORAGE.getEnergyStored());

        CompoundTag tankTag = new CompoundTag();
        FLUID_TANK.writeToNBT(registries, tankTag);
        tag.put("compact_refinery.fluid_tank", tankTag);

        tag.putInt("compact_refinery.progress", this.progress);
        tag.putInt("compact_refinery.max_progress", this.maxProgress);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        itemHandler.deserializeNBT(registries, tag.getCompound("compact_refinery.inventory"));
        ENERGY_STORAGE.setEnergy(tag.getInt("compact_refinery.energy"));

        FLUID_TANK.readFromNBT(registries, tag.getCompound("compact_refinery.fluid_tank"));

        this.progress = tag.getInt("compact_refinery.progress");
        this.maxProgress = tag.getInt("compact_refinery.max_progress");

        super.loadAdditional(tag, registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }

    /* GUI Methods */

    @Override
    public Component getDisplayName() {
        return Component.translatable("blockentity.thechaosengine.compact_refinery");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CompactRefineryMenu(containerId, playerInventory, this, this.data);
    }

    /* Worldly Container Methods */

    @Override
    public int @NotNull [] getSlotsForFace(Direction direction) {
        return new int[]{INPUT_SLOT, OUTPUT_SLOT};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slotIndex, ItemStack itemStack, @Nullable Direction direction) {
        Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        return direction != facing && slotIndex == INPUT_SLOT;
    }

    @Override
    public boolean canTakeItemThroughFace(int slotIndex, ItemStack itemStack, Direction direction) {
        return slotIndex == OUTPUT_SLOT;
    }

    @Override
    public int getContainerSize() {
        return this.itemHandler.getSlots();
    }

    @Override
    public boolean isEmpty() {
        return this.itemHandler.getStackInSlot(INPUT_SLOT).isEmpty() && this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slotIndex) {
        return this.itemHandler.getStackInSlot(slotIndex);
    }

    @Override
    public @NotNull ItemStack removeItem(int slotIndex, int count) {
        return this.itemHandler.extractItem(slotIndex, count, false);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slotIndex) {
        ItemStack stack = this.itemHandler.getStackInSlot(slotIndex);
        itemHandler.setStackInSlot(slotIndex, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slotIndex, ItemStack itemStack) {
        itemHandler.setStackInSlot(slotIndex,  itemStack);
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
}
