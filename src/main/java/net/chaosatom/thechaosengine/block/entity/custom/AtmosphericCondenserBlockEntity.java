package net.chaosatom.thechaosengine.block.entity.custom;

import net.chaosatom.thechaosengine.block.entity.ChaosEngineBlockEntities;
import net.chaosatom.thechaosengine.util.energy.EnergyStorage;
import net.chaosatom.thechaosengine.screen.custom.AtmosphericCondenserMenu;
import net.chaosatom.thechaosengine.util.ChaosEngineTags;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class AtmosphericCondenserBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider, WorldlyContainer{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /* Capabilities */
    public final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 0 ? 1 : super.getSlotLimit(slot);
        }
    };

    public FluidStack getFluid() {
        return FLUID_TANK.getFluid();
    }

    public IFluidHandler getTank(@Nullable Direction direction) {
        return FLUID_TANK;
    }

    // Labeled Slot Index
    private static final int INPUT_SLOT = 0;

    // Inventory & Processing Data Related
    private final ContainerData data;
    private int effectiveness = 0;
    private int maxEffectiveness = 250;
    private static final int FLUID_TRANSFER_AMOUNT = 500; // mB for cycle

    // Water Generation Related
    private int cooldown = 0;
    private static final int MAX_COOLDOWN = 5; // Total cycle time, runs every second (20 ticks)
    private static final int BASE_WATER_GENERATION = 15; // Base rate: 10 mB per cycle
    private static final int ENERGY_REQUIREMENT = 20; // Energy cost per cycle, FE
    private static final int SEA_LEVEL = 63;
    private static final double BONUS_PER_BLOCK_DOWN = 0.01;
    private static final double PENALTY_PER_BLOCK_UP = 0.005;
    private static final double MAX_HEIGHT_BONUS = 0.35; // Technically max depth but name is for consistency
    private static final double MAX_HEIGHT_PENALTY = 0.65;

    private int effectivenessUpdateCooldown = 0;
    private static final int EFFECTIVE_UPDATE_INTERVAL = 200; // Every 10 seconds, run a check to update effectiveness

    // GeckoLib Animation Setup

    private static final RawAnimation DEPLOY_SEQUENCE = RawAnimation.begin()
            .thenPlay("atmospheric_condenser.deploying")
            .thenLoop("atmospheric_condenser.idle");
    private static final RawAnimation DEPLOYED_LOOP = RawAnimation.begin()
            .thenLoop("atmospheric_condenser.idle");
    private static final RawAnimation UNDEPLOYED_LOOP = RawAnimation.begin()
            .thenLoop("atmospheric_condenser.undeployed");
    private static final RawAnimation RETRACT_SEQUENCE = RawAnimation.begin()
            .thenPlay("atmospheric_condenser.retracting")
            .thenLoop("atmospheric_condenser.undeployed");

    private enum AnimationState {
        UNDEPLOYED,
        DEPLOYING,
        DEPLOYED,
        RETRACTING
    }

    private AnimationState animState = AnimationState.UNDEPLOYED;
    private int deployTime = 0;
    private static final int DEPLOY_ANIMATION_LENGTH = 100; // Total time in ticks for deploy animation
    private int retractTime = 0;
    private static final int RETRACT_ANIMATION_LENGTH = 35;


    /* UTILITY */
    public AtmosphericCondenserBlockEntity(BlockPos pos, BlockState state) {
        super(ChaosEngineBlockEntities.ATMOSPHERIC_CONDENSER_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> AtmosphericCondenserBlockEntity.this.effectiveness;
                    case 1 -> AtmosphericCondenserBlockEntity.this.maxEffectiveness;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: AtmosphericCondenserBlockEntity.this.effectiveness = value; break;
                    case 1: AtmosphericCondenserBlockEntity.this.maxEffectiveness = value; break;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    private final EnergyStorage ENERGY_STORAGE = createEnergyStorage();
    private EnergyStorage createEnergyStorage() {
        return new EnergyStorage(32000, 320) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                assert getLevel() != null;
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        };
    }

    public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return this.ENERGY_STORAGE;
    }

    private final FluidTank FLUID_TANK = createFluidTank();
    private FluidTank createFluidTank() {
        return new FluidTank(16000) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                if(!level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return stack.getFluid() == Fluids.WATER;
            }
        };
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    /* Main Machine Logic */
    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        boolean isWorking;
        if (this.animState == AnimationState.DEPLOYING) {
            this.deployTime--;

            if (this.deployTime <= 0) {
                this.animState = AnimationState.DEPLOYED;
                setChanged();
            }
        } else if (this.animState == AnimationState.RETRACTING) {
            this.retractTime--;

            if (this.retractTime <= 0) {
                this.animState = AnimationState.UNDEPLOYED;
                setChanged();
            }
        }
        if (level.isClientSide() || this.animState != AnimationState.DEPLOYED) {
            return;
        }

        if (hasFluidHandlerInSlot()) {
            transferFluidFromTankToHandler();
        }

        if (this.effectivenessUpdateCooldown <= 0) {
            this.effectivenessUpdateCooldown = EFFECTIVE_UPDATE_INTERVAL;
            updateEffectiveness(level, blockPos);
        } else {
            this.effectivenessUpdateCooldown--;
        }

        if (cooldown > 0) {
            cooldown--;
            return;
        }
        this.cooldown = MAX_COOLDOWN;

        int waterAmount = getWaterAmount();

        if (canGenerate() && waterAmount > 0) {
            isWorking = true;
            this.ENERGY_STORAGE.extractEnergy(ENERGY_REQUIREMENT, false);
            this.FLUID_TANK.fill(new FluidStack(Fluids.WATER, waterAmount), IFluidHandler.FluidAction.EXECUTE);
            level.setBlock(blockPos, blockState.setValue(BlockStateProperties.LIT, isWorking), 3);
            setChanged();
        }
        pushFluidToOutputSides();
    }

    /* tick() method Helper Methods */

    private void transferFluidFromTankToHandler() {
        FluidActionResult result = FluidUtil.tryFillContainer(itemHandler.getStackInSlot(INPUT_SLOT), this.FLUID_TANK, Integer.MAX_VALUE, null, true);
        if (result.result != ItemStack.EMPTY) {
            itemHandler.setStackInSlot(INPUT_SLOT, result.result);
        }
    }

    private boolean hasFluidHandlerInSlot() {
        return !itemHandler.getStackInSlot(INPUT_SLOT).isEmpty()
                && itemHandler.getStackInSlot(INPUT_SLOT).getCapability(Capabilities.FluidHandler.ITEM, null) != null
                && (itemHandler.getStackInSlot(INPUT_SLOT).getCapability(Capabilities.FluidHandler.ITEM, null).getFluidInTank(INPUT_SLOT).isEmpty() ||
                FluidUtil.tryFluidTransfer(itemHandler.getStackInSlot(INPUT_SLOT).getCapability(Capabilities.FluidHandler.ITEM, null),
                        FLUID_TANK, Integer.MAX_VALUE, false) != FluidStack.EMPTY);
    }

    private boolean canGenerate() {
        return this.ENERGY_STORAGE.getEnergyStored() >= ENERGY_REQUIREMENT && this.FLUID_TANK.getSpace() > 0;
    }

    private void updateEffectiveness(Level level, BlockPos pos) {
        double effectivenessMultiplier = 1.0;
        Holder<Biome> biome = level.getBiome(pos);

        // Hope this is one of the better ways of doing this...
        // Adds (or subtracts) a bonus to water generation based on biome the condenser is in
        if (biome.is(ChaosEngineTags.Biomes.EXTREMELY_WET)) {
            effectivenessMultiplier += 1.05;
        } else if (biome.is(ChaosEngineTags.Biomes.VERY_WET)) {
            effectivenessMultiplier += 0.88;
        } else if (biome.is(ChaosEngineTags.Biomes.WET)) {
            effectivenessMultiplier += 0.74;
        } else if (biome.is(ChaosEngineTags.Biomes.TEMPERATE)) {
            effectivenessMultiplier += 0.50;
        } else if (biome.is(ChaosEngineTags.Biomes.DRY)) {
            effectivenessMultiplier -= 0.39;
        } else if (biome.is(ChaosEngineTags.Biomes.ARID)) {
            effectivenessMultiplier -= 0.85;
        } else {
            effectivenessMultiplier += 0.15;
        }

        // Adds (or subtracts) a bonus based on the vertical position of the condenser
        int heightDifference = SEA_LEVEL - pos.getY();
        double heightModifier;
        if (heightDifference > 0) {
            heightModifier = heightDifference * BONUS_PER_BLOCK_DOWN; // Decreasing effectiveness bonus above sea level
        } else {
            heightModifier = heightDifference * PENALTY_PER_BLOCK_UP; // Increasing effectiveness bonus below sea level
        }
        heightModifier = Math.max(-MAX_HEIGHT_PENALTY, Math.min(MAX_HEIGHT_BONUS, heightModifier));
        effectivenessMultiplier += heightModifier;

        // Adds a bonus if its raining
        if (level.isRaining()) {
            effectivenessMultiplier += 0.25;
        } else if (level.isThundering()) {
            effectivenessMultiplier += 0.35;
        }

        // Adds a bonus if it's underground (sort of)
        if (!level.canSeeSky(pos) && pos.getY() < 63) {
            effectivenessMultiplier += 0.45;
        }

        effectivenessMultiplier = Math.max(0, effectivenessMultiplier);
        this.effectiveness = (int) (effectivenessMultiplier * 100);
    }

    private int getWaterAmount() {
        double effectiveMultiplier = (double) this.effectiveness / 100;
        return (int) Math.floor(BASE_WATER_GENERATION * effectiveMultiplier);
    }

    private void pushFluidToOutputSides() {
        if (this.FLUID_TANK.getFluidAmount() <= 0) { return; }

        List<IFluidHandler> validNeighbors = new ArrayList<>(); // Create new array list ready for valid input neighbors
        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction[] outputSides = { facing.getClockWise(), facing.getCounterClockWise() }; // Only the left and right side of machine will push liquids
        for (Direction side : outputSides) {
            BlockPos neighborPos = worldPosition.relative(side);
            IFluidHandler neighborFluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, neighborPos, side.getOpposite());

            // Checks if the neighbor exists AND has a valid fluid tank that can be filled. If true, add to list for distribution
            if (neighborFluidHandler != null) {
                if (neighborFluidHandler.fill(this.FLUID_TANK.getFluid(), IFluidHandler.FluidAction.SIMULATE) > 0) {
                    validNeighbors.add(neighborFluidHandler);
                }
            }
        }
         // Check if there are any valid neighbors in list
        if (!validNeighbors.isEmpty()) {
            int amountToPush = Math.min(this.FLUID_TANK.getFluidAmount(), FLUID_TRANSFER_AMOUNT);
            int amountPerNeighbor = amountToPush / validNeighbors.size(); // Equally distribute amount between both sides (if valid)

            // If there is not enough to push, exit method with no fluid exchange
            if (amountPerNeighbor <= 0) { return; }

            for (IFluidHandler neighbor : validNeighbors) {
                FluidStack toSend = this.FLUID_TANK.getFluid().copy();
                toSend.setAmount(amountPerNeighbor);

                // Set amount to send to valid neighbor
                int accepted = neighbor.fill(toSend, IFluidHandler.FluidAction.EXECUTE);
                // If there is some amount that can be sent, proceed to send from condenser's tank to neighbor
                if (accepted > 0) {
                    this.FLUID_TANK.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
                    setChanged();
                }
            }
        }
    }

    /* Animation Related Helper Methods */

    public void startDeployment() {
        // If the current animation state is undeployed...
        if (this.animState == AnimationState.UNDEPLOYED) {
            // Change to deploying state and
            this.animState = AnimationState.DEPLOYING;
            // set deployTime to length of deploying animation
            this.deployTime = DEPLOY_ANIMATION_LENGTH;
            setChanged();
        }
    }

    public void startRetraction() {
        // Similar style to startDeployment() but checks if condenser is deployed and resets back it to undeployed
        if (this.animState == AnimationState.DEPLOYED) {
            this.animState = AnimationState.RETRACTING;
            this.retractTime = RETRACT_ANIMATION_LENGTH;
            setChanged();
        }
    }

    /* General Block Entity Methods */

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("atmospheric_condenser.inventory",  itemHandler.serializeNBT(registries));
        tag.putInt("atmospheric_condenser.energy", ENERGY_STORAGE.getEnergyStored());
        tag = FLUID_TANK.writeToNBT(registries, tag);

        tag.putInt("atmospheric_condenser.effectiveness", effectiveness);
        tag.putInt("atmospheric_condenser.max_effectiveness", maxEffectiveness);
        tag.putInt("atmospheric_condenser.cooldown", cooldown);
        tag.putInt("atmospheric_condenser.effectivenessUpdateCooldown", effectivenessUpdateCooldown);

        tag.putInt("atmospheric_condenser.animation_state", this.animState.ordinal());
        tag.putInt("atmospheric_condenser.deploy_time", this.deployTime);
        tag.putInt("atmospheric_condenser.retract_time", this.retractTime);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        itemHandler.deserializeNBT(registries, tag.getCompound("atmospheric_condenser.inventory"));
        ENERGY_STORAGE.setEnergy(tag.getInt("atmospheric_condenser.energy"));
        FLUID_TANK.readFromNBT(registries, tag);

        this.effectiveness = tag.getInt("atmospheric_condenser.effectiveness");
        this.maxEffectiveness = tag.getInt("atmospheric_condenser.max_effectiveness");
        this.cooldown = tag.getInt("atmospheric_condenser.cooldown");
        this.effectivenessUpdateCooldown = tag.getInt("atmospheric_condenser.effectivenessUpdateCooldown");

        this.animState = AnimationState.values()[tag.getInt("atmospheric_condenser.animation_state")];
        this.deployTime = tag.getInt("atmospheric_condenser.deploy_time");
        this.retractTime = tag.getInt("atmospheric_condenser.retract_time");

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

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }

    /* GUI Method(s) */


    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("blockentity.thechaosengine.atmospheric_condenser");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AtmosphericCondenserMenu(containerId, playerInventory, this, this.data);
    }

    /* Worldly Container Methods */

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return new int[]{INPUT_SLOT};
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
        return slotIndex == INPUT_SLOT;
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

    /* GeckoLib Animatable Block Entity Methods */

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "loop_controller",
                0, state -> switch (state.getAnimatable().animState) {
            case UNDEPLOYED ->  state.setAndContinue(UNDEPLOYED_LOOP);
            case DEPLOYING -> state.setAndContinue(DEPLOY_SEQUENCE);
            case DEPLOYED -> state.setAndContinue(DEPLOYED_LOOP);
            case RETRACTING -> state.setAndContinue(RETRACT_SEQUENCE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}