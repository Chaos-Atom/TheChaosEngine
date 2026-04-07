package net.chaosatom.thechaosengine.block.entity.custom;

import net.chaosatom.thechaosengine.block.entity.ChaosEngineBlockEntities;
import net.chaosatom.thechaosengine.recipe.ChaosEngineRecipes;
import net.chaosatom.thechaosengine.recipe.PyrolizingRecipe;
import net.chaosatom.thechaosengine.recipe.SingleItemRecipeInput;
import net.chaosatom.thechaosengine.screen.custom.PyrolysisCrucibleMenu;
import net.chaosatom.thechaosengine.util.ChaosEngineTags;
import net.chaosatom.thechaosengine.util.energy.EnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

public class PyrolysisCrucibleBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider, WorldlyContainer {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Labeled Slot Indices
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    // Inventory & Processing Data Related
    private final ContainerData data;
    private int progress = 0;
    private int maxProgress;
    private static final int DEFAULT_MAX_PROGRESS = 80;

    // Environmental Heating Bonus Related
    private int heatBonusUpdateCooldown = 0;
    private static final int HEAT_BONUS_UPDATE_INTERVAL = 20;
    private int heatBonus = 0;
    private int maxHeatBonus = 7;

    public PyrolysisCrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(ChaosEngineBlockEntities.PYROLYSIS_CRUCIBLE_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> PyrolysisCrucibleBlockEntity.this.progress;
                    case 1 -> PyrolysisCrucibleBlockEntity.this.maxProgress;
                    case 2 -> PyrolysisCrucibleBlockEntity.this.heatBonus;
                    case 3 -> PyrolysisCrucibleBlockEntity.this.maxHeatBonus;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: PyrolysisCrucibleBlockEntity.this.progress = value; break;
                    case 1: PyrolysisCrucibleBlockEntity.this.maxProgress = value; break;
                    case 2: PyrolysisCrucibleBlockEntity.this.heatBonus = value; break;
                    case 3: PyrolysisCrucibleBlockEntity.this.maxHeatBonus = value; break;
                }
            }

            @Override
            public int getCount() { return 4; }
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
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(),3);
            }
        };
    }

    public IEnergyStorage getEnergyStorage(Direction direction) {
        return this.ENERGY_STORAGE;
    }

    /* GeckoLib Animation Methods */
    private static final RawAnimation DEPLOY_SEQUENCE = RawAnimation.begin()
            .thenPlay("pyrolysis_crucible.deploying")
            .thenLoop("pyrolysis_crucible.idle");
    private static final RawAnimation DEPLOYED_LOOP = RawAnimation.begin()
            .thenLoop("pyrolysis_crucible.idle");
    private static final RawAnimation WORKING_LOOP = RawAnimation.begin()
            .thenLoop("pyrolysis_crucible.working");
    private static final RawAnimation RETRACT_SEQUENCE = RawAnimation.begin()
            .thenPlay("pyrolysis_crucible.retracting")
            .thenLoop("pyrolysis_crucible.undeployed");
    private static final RawAnimation UNDEPLOYED_LOOP = RawAnimation.begin()
            .thenLoop("pyrolysis_crucible.undeployed");

    public enum AnimationState {
        DEPLOYING,
        DEPLOYED,
        WORKING,
        RETRACTING,
        UNDEPLOYED
    }

    private AnimationState animState = AnimationState.UNDEPLOYED;
    private int deployTime = 0;
    private static final int DEPLOY_ANIMATION_LENGTH = 90; // 4.5 seconds
    private int retractTime = 0;
    private static final int RETRACT_ANIMATION_LENGTH = 75; // 3.75 seconds

    public AnimationState getAnimState() {
        return this.animState;
    }

    // Animation Helper Methods
    public void startPyrolysisCrucibleDeployment() {
        if (this.animState == AnimationState.UNDEPLOYED) {
            this.animState = AnimationState.DEPLOYING;
            this.deployTime = DEPLOY_ANIMATION_LENGTH;
            setChanged();
        }
    }

    public void startPyrolysisCrucibleRetraction() {
        if (this.animState == AnimationState.DEPLOYED) {
            this.animState = AnimationState.RETRACTING;
            this.retractTime = RETRACT_ANIMATION_LENGTH;
            setChanged();
        }
    }

    /* Main Machine Logic */
    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        Optional<RecipeHolder<PyrolizingRecipe>> recipe = getCurrentRecipe();
        boolean wasWorkingThisTick = false;

        if (this.animState == AnimationState.DEPLOYING) {
            this.deployTime--;

            if (this.deployTime <= 0) {
                this.animState = AnimationState.DEPLOYED;
                setChanged(level, blockPos, blockState);
            }
        } else if (this.animState == AnimationState.RETRACTING) {
            this.retractTime--;
            if (this.retractTime <= 0) {
                this.animState = AnimationState.UNDEPLOYED;
                setChanged(level, blockPos, blockState);
            }
        }

        if (level.isClientSide()) {
            return;
        }

        if (heatBonusUpdateCooldown <= 0) {
            this.heatBonusUpdateCooldown = HEAT_BONUS_UPDATE_INTERVAL;
            updateHeatBonus(level, blockPos);
        } else {
            this.heatBonusUpdateCooldown--;
        }

        if (recipe.isEmpty() || !isOutputSlotEmptyOrReceivable()) {
            setChanged(level, blockPos, blockState);
            resetProgress();
            return;
        }

        if (this.animState == AnimationState.DEPLOYED || this.animState == AnimationState.WORKING) {
            if (this.ENERGY_STORAGE.getEnergyStored() >= craftingEnergyCost() && hasRecipe()) {
                updateProgressWithHeatBonus();
                if (hasItemInOutput()) {
                    pushOutputs();
                }

                this.animState = AnimationState.WORKING;
                wasWorkingThisTick = true;
                useEnergyAndProgress();
                setChanged(level, blockPos, blockState);

                if (hasCraftingFinished()) {
                    craftItem();
                    resetProgress();
                    wasWorkingThisTick = false;
                }
                if (canStillWork()) {
                    this.animState = AnimationState.WORKING;
                    wasWorkingThisTick = true;
                } else {
                    this.animState = AnimationState.DEPLOYED;
                }
            }
        }

        boolean isLit = wasWorkingThisTick;
        if (blockState.getValue(BlockStateProperties.LIT) != isLit) {
            level.setBlock(blockPos, blockState.setValue(BlockStateProperties.LIT, isLit), 3);
        }
        pushOutputs();
    }

    /* Crafting Helper Methods */
    private Optional<RecipeHolder<PyrolizingRecipe>> getCurrentRecipe() {
        assert this.level != null;
        return this.level.getRecipeManager()
                .getRecipeFor(ChaosEngineRecipes.PYROLYSIS_CRUCIBLE_TYPE.get(),
                        new SingleItemRecipeInput(itemHandler.getStackInSlot(INPUT_SLOT)), level);
    }

    private int craftingEnergyCost() {
        Optional<RecipeHolder<PyrolizingRecipe>> recipe = getCurrentRecipe();
        return recipe.get().value().processTime() * recipe.get().value().processTime();
    }

    private boolean hasItemInOutput() {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() > 0;
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

    private boolean hasRecipe() {
        Optional<RecipeHolder<PyrolizingRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return false;
        }

        ItemStack output = recipe.get().value().result();
        this.maxProgress = recipe.get().value().processTime();

        return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemInputIntoOutputSlot(output) &&
                this.ENERGY_STORAGE.getEnergyStored() >= craftingEnergyCost();
    }

    private void useEnergyAndProgress() {
        Optional<RecipeHolder<PyrolizingRecipe>> recipe = getCurrentRecipe();
        int specificEnergyCost = recipe.get().value().energy();
        this.ENERGY_STORAGE.extractEnergy(specificEnergyCost, false);
        progress++;
    }

    private boolean hasCraftingFinished() {
        Optional<RecipeHolder<PyrolizingRecipe>> recipe = getCurrentRecipe();
        return this.progress >= maxProgress;
    }

    private void craftItem() {
        Optional<RecipeHolder<PyrolizingRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return;
        }
        ItemStack output = recipe.get().value().result();

        itemHandler.extractItem(INPUT_SLOT, 1, false);
        itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(output.getItem(),
                itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount()));
    }

    private void resetProgress() {
        Optional<RecipeHolder<PyrolizingRecipe>> recipe = getCurrentRecipe();
        this.progress = 0;
        this.maxProgress = recipe.map(recipeHolder ->
                recipeHolder.value().processTime()).orElse(DEFAULT_MAX_PROGRESS);
    }

    private boolean canStillWork() {
        return itemHandler.getStackInSlot(INPUT_SLOT).getCount() > 0 && hasRecipe();
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        Optional<RecipeHolder<PyrolizingRecipe>> recipe = getCurrentRecipe();
        ItemStack output = recipe.get().value().result();

        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() <
                        this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize() + output.getCount();
    }

    private void updateHeatBonus(Level level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos.below());
        Holder<Block> block = level.getBlockState(pos.below()).getBlockHolder();

        // Is there a way to do this with switch-case?
        if (block.is(ChaosEngineTags.Blocks.HEAT_BONUS_LEVEL_ONE)) {
            heatBonus = 1;
        } else if (block.is(ChaosEngineTags.Blocks.HEAT_BONUS_LEVEL_TWO)) {
            heatBonus = 2;
        } else if (block.is(ChaosEngineTags.Blocks.HEAT_BONUS_LEVEL_THREE)) {
            heatBonus = 3;
        } else if (block.is(ChaosEngineTags.Blocks.HEAT_BONUS_LEVEL_FOUR)) {
            heatBonus = 4;
        } else if (block.is(ChaosEngineTags.Blocks.HEAT_BONUS_LEVEL_FIVE)) {
            heatBonus = 5;
        } else if (block.is(ChaosEngineTags.Blocks.HEAT_BONUS_LEVEL_SIX)) {
            heatBonus = 6;
        } else if (block.is(ChaosEngineTags.Blocks.HEAT_BONUS_LEVEL_SEVEN)) {
            heatBonus = 7;
        } else {
            heatBonus = 0;
        }
    }

    private void updateProgressWithHeatBonus() {
        Optional<RecipeHolder<PyrolizingRecipe>> recipe = getCurrentRecipe();
        maxProgress = recipe.get().value().processTime() - (heatBonus * 15);
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
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("pyrolysis_crucible.energy", ENERGY_STORAGE.getEnergyStored());

        tag.putInt("pyrolysis_crucible.progress", progress);
        tag.putInt("pyrolysis_crucible.max_progress", maxProgress);
        tag.putInt("pyrolysis_crucible.heat_bonus", heatBonus);
        tag.putInt("pyrolysis_crucible.max_heat_bonus", maxHeatBonus);

        tag.putInt("pyrolysis_crucible.animation_state", this.animState.ordinal());
        tag.putInt("pyrolysis_crucible.deploy_time", this.deployTime);
        tag.putInt("pyrolysis_crucible.retract_time", this.retractTime);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        ENERGY_STORAGE.setEnergy(tag.getInt("pyrolysis_crucible.energy"));

        progress = tag.getInt("pyrolysis_crucible.progress");
        maxProgress = tag.getInt("pyrolysis_crucible.max_progress");
        heatBonus = tag.getInt("pyrolysis_crucible.heat_bonus");
        maxHeatBonus = tag.getInt("pyrolysis_crucible.max_heat_bonus");

        this.animState = AnimationState.values()[tag.getInt("pyrolysis_crucible.animation_state")];
        this.deployTime = tag.getInt("pyrolysis_crucible.deploy_time");
        this.retractTime = tag.getInt("pyrolysis_crucible.retract_time");

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
        return Component.translatable("blockentity.thechaosengine.pyrolysis_crucible");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new PyrolysisCrucibleMenu(containerId, inventory, this, this.data);
    }

    /* Worldly Container Methods */
    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{INPUT_SLOT, OUTPUT_SLOT};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slotIndex, ItemStack itemStack, @Nullable Direction side) {
        Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (side == facing) {
            return false;
        }
        return slotIndex == INPUT_SLOT;
    }

    @Override
    public boolean canTakeItemThroughFace(int slotIndex, ItemStack itemStack, Direction side) {
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
        return itemHandler.extractItem(slotIndex, count, false);
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
        return player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5,
                this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    /* GeckoLib Animatable Block Entity Methods */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main_controller",
                0, state -> switch (state.getAnimatable().animState) {
            case DEPLOYING -> state.setAndContinue(DEPLOY_SEQUENCE);
            case DEPLOYED -> state.setAndContinue(DEPLOYED_LOOP);
            case WORKING -> state.setAndContinue(WORKING_LOOP);
            case RETRACTING -> state.setAndContinue(RETRACT_SEQUENCE);
            case UNDEPLOYED -> state.setAndContinue(UNDEPLOYED_LOOP);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    /* Sided Inventory Method */
    private void pushOutputs() {
        ItemStack outputStack = itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (outputStack.isEmpty()) {
            return;
        }

        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction[] outputDirections = { Direction.DOWN, facing.getCounterClockWise() }; // Bottom & Left side of block

        for (Direction direction : outputDirections) {
            assert level != null;
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
            if (neighbor == null) {
                continue;
            }

            IItemHandler neighborHandler = level.getCapability(Capabilities.ItemHandler.BLOCK,
                    worldPosition.relative(direction),
                    direction.getOpposite());

            if (neighborHandler != null) {
                ItemStack remainder = ItemHandlerHelper.insertItem(neighborHandler, outputStack, false);

                if (remainder.getCount() < outputStack.getCount()) {
                    itemHandler.setStackInSlot(OUTPUT_SLOT, remainder);
                    setChanged();
                    return;
                }
            }
        }
    }
}
