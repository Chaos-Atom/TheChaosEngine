package net.chaosatom.thechaosengine.block.entity.custom;

import net.chaosatom.thechaosengine.block.entity.ChaosEngineBlockEntities;
import net.chaosatom.thechaosengine.recipe.ChaosEngineRecipes;
import net.chaosatom.thechaosengine.recipe.SuspensionMixerRecipe;
import net.chaosatom.thechaosengine.screen.custom.SuspensionMixerMenu;
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
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;

public class SuspensionMixerBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider, WorldlyContainer {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final int INPUT_SLOT = 0;
    private final ContainerData data;
    private int progress = 0;
    private int maxProgress;
    private static final int FLUID_TRANSFER_AMOUNT = 500;

    public SuspensionMixerBlockEntity(BlockPos pos, BlockState state) {
        super(ChaosEngineBlockEntities.SUSPENSION_MIXER_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SuspensionMixerBlockEntity.this.progress;
                    case 1 -> SuspensionMixerBlockEntity.this.maxProgress;
                    case 2 -> SuspensionMixerBlockEntity.this.FLUID_TANK_INPUT.getFluidAmount();
                    case 3 -> SuspensionMixerBlockEntity.this.FLUID_TANK_OUTPUT.getFluidAmount();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: SuspensionMixerBlockEntity.this.progress = value; break;
                    case 1: SuspensionMixerBlockEntity.this.maxProgress = value; break;
                    case 2: break;
                    case 3: break;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    /* Capabilities */
    public final ItemStackHandler itemHandler = new ItemStackHandler(1) {
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

    private final FluidTank FLUID_TANK_INPUT = new FluidTank(8000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == Fluids.WATER;
        }
    };

    private final FluidTank FLUID_TANK_OUTPUT = new FluidTank(8000) {
        @Override
        protected void onContentsChanged() {
            if (level != null && level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public FluidStack getInputFluid() {
        return FLUID_TANK_INPUT.getFluid();
    }

    public FluidStack getOutputFluid() {
        return FLUID_TANK_OUTPUT.getFluid();
    }

    public IFluidHandler getInputTank(Direction direction) {
        return FLUID_TANK_INPUT;
    }

    public IFluidHandler getOutputTank(Direction direction) {
        return FLUID_TANK_OUTPUT;
    }

    /* GeckoLib Animation Setup */
    private static final RawAnimation DEPLOY_SEQUENCE = RawAnimation.begin()
            .thenPlay("suspension_mixer.deploying")
            .thenLoop("suspension_mixer.active");
    private static final RawAnimation DEPLOYED_LOOP = RawAnimation.begin()
            .thenLoop("suspension_mixer.active");
    private static final RawAnimation UNDEPLOYED_LOOP = RawAnimation.begin()
            .thenLoop("suspension_mixer.undeployed");
    private static final RawAnimation WORKING_LOOP = RawAnimation.begin()
            .thenLoop("suspension_mixer.working");
    private static final RawAnimation RETRACT_SEQUENCE = RawAnimation.begin()
            .thenPlay("suspension_mixer.retracting")
            .thenLoop("suspension_mixer.undeployed");

    public enum AnimationState {
        DEPLOYING,
        DEPLOYED,
        UNDEPLOYED,
        WORKING,
        RETRACTING
    }

    private AnimationState animState = AnimationState.UNDEPLOYED;
    private int deployTime = 0;
    private static final int DEPLOY_ANIMATION_LENGTH = 125; // 6.25 seconds
    private int retractTime = 0;
    private static final int RETRACT_ANIMATION_LENGTH = 55; // 2.25 seconds

    public AnimationState getAnimState() {
        return this.animState;
    }

    // Animation Related Helper Methods
    public void startMixerDeployment() {
        // If the current animation state is undeployed...
        if (this.animState == AnimationState.UNDEPLOYED) {
            // Change to deploying state and
            this.animState = AnimationState.DEPLOYING;
            // set deployTime to length of deploying animation
            this.deployTime = DEPLOY_ANIMATION_LENGTH;
            setChanged();
        }
    }

    public void startMixerRetraction() {
        // Similar style to startMixerDeployment() but checks if mixer is deployed and resets back it to undeployed
        if (this.animState == AnimationState.DEPLOYED) {
            this.animState = AnimationState.RETRACTING;
            this.retractTime = RETRACT_ANIMATION_LENGTH;
            setChanged();
        }
    }

    /* Main Machine Logic */
    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
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

        if (this.animState == AnimationState.DEPLOYED || this.animState == AnimationState.WORKING) {
            Optional<RecipeHolder<SuspensionMixerRecipe>> recipe = getCurrentRecipe();
            if (recipe.isPresent() && hasResources(recipe.get().value())) {
                SuspensionMixerRecipe currentRecipe = recipe.get().value();
                this.animState = AnimationState.WORKING;

                this.ENERGY_STORAGE.extractEnergy(currentRecipe.energy(), false);
                this.progress++;
                setChanged(level, blockPos, blockState);
                if (this.progress >= this.maxProgress) {
                    craftItem(recipe.get());
                    resetProgress();
                }
            } else {
                resetProgress();
                this.animState = AnimationState.DEPLOYED;
            }
        }
        boolean isLit = this.animState == AnimationState.WORKING;
        level.setBlock(blockPos, blockState.setValue(BlockStateProperties.LIT, isLit), 3);
    }

    /* Custom Crafting Logic via Helper methods */

    private Optional<RecipeHolder<SuspensionMixerRecipe>> getCurrentRecipe() {
        assert this.level != null;
        return this.level.getRecipeManager()
                .getAllRecipesFor(ChaosEngineRecipes.SUSPENSION_MIXER_TYPE.get()).stream().filter(
                        recipeHolder -> {
                            SuspensionMixerRecipe recipe = recipeHolder.value();
                            // Checks if item in item slot matches a valid recipe
                            boolean itemMatches = recipe.itemIngredient().test(this.itemHandler.getStackInSlot(INPUT_SLOT));
                            // Checks if fluid in fluid tank matches
                            boolean fluidMatches = this.FLUID_TANK_INPUT.getFluid().is(recipe.fluidIngredient().getFluid()) &&
                                 this.FLUID_TANK_INPUT.getFluidAmount() >= recipe.fluidIngredient().getAmount();
                            return itemMatches && fluidMatches;
                        }).findFirst();
    }

    private boolean hasResources(SuspensionMixerRecipe recipe) {
        boolean hasEnoughEnergy = this.ENERGY_STORAGE.getEnergyStored() >= recipe.energy();
        boolean hasSpaceInOutput = this.FLUID_TANK_OUTPUT.fill(recipe.output(), IFluidHandler.FluidAction.SIMULATE) ==
                recipe.output().getAmount();
        this.maxProgress = recipe.processTime();

        return hasEnoughEnergy && hasSpaceInOutput;
    }

    private void craftItem(RecipeHolder<SuspensionMixerRecipe> recipeHolder) {
        SuspensionMixerRecipe recipe = recipeHolder.value();

        this.itemHandler.extractItem(INPUT_SLOT, 1, false);
        this.FLUID_TANK_INPUT.drain(recipe.fluidIngredient().getAmount(), IFluidHandler.FluidAction.EXECUTE);
        this.FLUID_TANK_OUTPUT.fill(recipe.output(), IFluidHandler.FluidAction.EXECUTE);
    }

    private void resetProgress() {
        this.progress = 0;
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
        tag.put("suspension_mixer.inventory", itemHandler.serializeNBT(registries));
        tag.putInt("suspension_mixer.energy", ENERGY_STORAGE.getEnergyStored());

        CompoundTag inputTankTag = new CompoundTag();
        FLUID_TANK_INPUT.writeToNBT(registries, inputTankTag);
        tag.put("suspension_mixer.input_tank", inputTankTag);
        CompoundTag outputTankTag = new CompoundTag();
        FLUID_TANK_OUTPUT.writeToNBT(registries, outputTankTag);
        tag.put("suspension_mixer.output_tank", outputTankTag);


        tag.putInt("suspension_mixer.progress", this.progress);
        tag.putInt("suspension_mixer.max_progress", this.maxProgress);

        tag.putInt("suspension_mixer.animation_state", this.animState.ordinal());
        tag.putInt("suspension_mixer.deploy_time", this.deployTime);
        tag.putInt("suspension_mixer.retract_time", this.retractTime);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        itemHandler.deserializeNBT(registries, tag.getCompound("suspension_mixer.inventory"));
        ENERGY_STORAGE.setEnergy(tag.getInt("suspension_mixer.energy"));

        FLUID_TANK_INPUT.readFromNBT(registries, tag.getCompound("suspension_mixer.input_tank"));
        FLUID_TANK_OUTPUT.readFromNBT(registries, tag.getCompound("suspension_mixer.output_tank"));

        this.progress = tag.getInt("suspension_mixer.progress");
        this.maxProgress = tag.getInt("suspension_mixer.max_progress");

        this.animState = AnimationState.values()[tag.getInt("suspension_mixer.animation_state")];
        this.deployTime = tag.getInt("suspension_mixer.deploy_time");
        this.retractTime = tag.getInt("suspension_mixer.retract_time");

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
        return Component.translatable("blockentity.thechaosengine.suspension_mixer");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new SuspensionMixerMenu(containerId, inventory, this, this.data);
    }

    /* Worldly Container Methods */

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return new int[]{INPUT_SLOT};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slotIndex, ItemStack itemStack, @Nullable Direction direction) {
        Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (direction != facing.getOpposite()) {
            return false;
        }
        return slotIndex == INPUT_SLOT;
    }

    @Override
    public boolean canTakeItemThroughFace(int slotIndex, ItemStack itemStack, Direction direction) {
        return false; // Has no item outputs to take from
    }

    @Override
    public int getContainerSize() {
        return itemHandler.getSlots();
    }

    @Override
    public boolean isEmpty() {
        return this.itemHandler.getStackInSlot(INPUT_SLOT).isEmpty();
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
        return player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
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
                0, state ->  switch (state.getAnimatable().animState) {
            case DEPLOYING -> state.setAndContinue(DEPLOY_SEQUENCE);
            case DEPLOYED -> state.setAndContinue(DEPLOYED_LOOP);
            case UNDEPLOYED -> state.setAndContinue(UNDEPLOYED_LOOP);
            case RETRACTING -> state.setAndContinue(RETRACT_SEQUENCE);
            case WORKING -> state.setAndContinue(WORKING_LOOP);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
