package net.chaosatom.thechaosengine.block.entity.custom;

import net.chaosatom.thechaosengine.block.entity.ChaosEngineBlockEntities;
import net.chaosatom.thechaosengine.util.energy.EnergyStorage;
import net.chaosatom.thechaosengine.recipe.FuelItemRecipes;
import net.chaosatom.thechaosengine.screen.custom.CompactCoalGeneratorMenu;
import net.chaosatom.thechaosengine.util.ChaosEngineTags;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompactCoalGeneratorBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            assert level != null;
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private static final int INPUT_SLOT = 0;

    protected final ContainerData data;
    private int burnProgress = 0; // in ticks
    private int maxBurnProgress = 0;
    private int energyPerTick = 0;

    private static final int ENERGY_TRANSFER_AMOUNT = 320;

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

    public CompactCoalGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ChaosEngineBlockEntities.COMPACT_COAL_GENERATOR_BE.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> CompactCoalGeneratorBlockEntity.this.burnProgress;
                    case 1 -> CompactCoalGeneratorBlockEntity.this.maxBurnProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> CompactCoalGeneratorBlockEntity.this.burnProgress = pValue;
                    case 1 -> CompactCoalGeneratorBlockEntity.this.maxBurnProgress = pValue;
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

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Compact Coal Generator");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return new CompactCoalGeneratorMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        // Continues to burn the fuel, important failsafe if the world was saved mid-operation on a final item
        if (burnProgress > 0) {
            burnProgress--;
            fillUpOnEnergy();
            setChanged();
        }

        // Starts a new fuel burn cycle when block is idle and has correct fuel item according to hashmap
        if (burnProgress <= 0 && hasFuelItemInSlot() && ENERGY_STORAGE.getEnergyStored() < 32000) {
            Item fuelItem = this.itemHandler.getStackInSlot(INPUT_SLOT).getItem(); // Assigns item based on slot's item
            // Grabs item-specific data assigned in map & record from fuelItem
            FuelItemRecipes.FuelData fuelData = FuelItemRecipes.FUEL_STATS.get(fuelItem);
            if (fuelData != null) {
                // Consumes one item (confirmed to be valid fuel) from generator's slot
                this.itemHandler.extractItem(INPUT_SLOT, 1, false);
                // Updates all the initialized variables to the corresponding specifics from each fuel item
                this.burnProgress = fuelData.specificBurnProgress();
                this.maxBurnProgress = fuelData.specificBurnProgress();
                this.energyPerTick = fuelData.specificEnergyPerTick();
                setChanged();
            }
        }

        // Checks if the generator is running, if it is, set block state to LIT for particle effects, sounds, etc.
        boolean isLit = burnProgress > 0;
        if (blockState.getValue(BlockStateProperties.LIT) != isLit) {
            level.setBlock(blockPos, blockState.setValue(BlockStateProperties.LIT, isLit), 3);
        }
        pushEnergyToOutputSide(level);
    }

    private void pushEnergyToOutputSide(Level level) {
        if (this.ENERGY_STORAGE.getEnergyStored() <= 0) {
            return;
        }

        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction rightSide = facing.getClockWise();
        assert level != null;
        IEnergyStorage neighborEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                worldPosition.relative(rightSide),
                rightSide.getOpposite());
        if (neighborEnergy != null && neighborEnergy.canReceive()) {
            int toSend = this.ENERGY_STORAGE.extractEnergy(ENERGY_TRANSFER_AMOUNT, true);
            int toReceive = neighborEnergy.receiveEnergy(toSend, false);
            this.ENERGY_STORAGE.extractEnergy(toReceive, false);
            setChanged();
        }
    }

    /* Will use this variant for something else later (A battery maybe)?
    private void pushEnergyToAllNeighbors() {
        if (this.ENERGY_STORAGE.getEnergyStored() <= 0) {
            return;
        }

        for (Direction direction : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
            if (neighbor == null || neighbor.equals(this)) {
                continue;
            }

            IEnergyStorage neighborEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                    worldPosition.relative(direction),
                    direction.getOpposite());

            if (neighborEnergy != null && neighborEnergy.canReceive()) {
                int toSend = this.ENERGY_STORAGE.extractEnergy(ENERGY_TRANSFER_AMOUNT, true);
                int toReceive = neighborEnergy.receiveEnergy(toSend, false);
                this.ENERGY_STORAGE.extractEnergy(toReceive, false);
                setChanged();
            }
        }
    }
    */

    private boolean hasFuelItemInSlot() {
        return this.itemHandler.getStackInSlot(INPUT_SLOT).is(ChaosEngineTags.Items.COAL_GENERATOR_FUEL);
    }

    private void fillUpOnEnergy() {
        this.ENERGY_STORAGE.receiveEnergy(this.energyPerTick, false);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        tag.put("compact_coal_generator.inventory", itemHandler.serializeNBT(registries));

        tag.putInt("compact_coal_generator.burn_progress", burnProgress);
        tag.putInt("compact_coal_generator.max_burn_progress", maxBurnProgress);

        tag.putInt("compact_coal_generator.energy_stored", ENERGY_STORAGE.getEnergyStored());
        tag.putInt("compact_coal_generator.energy_per_tick", energyPerTick);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("compact_coal_generator.inventory"));

        burnProgress = tag.getInt("compact_coal_generator.burn_progress");
        maxBurnProgress = tag.getInt("compact_coal_generator.max_burn_progress");

        ENERGY_STORAGE.setEnergy(tag.getInt("compact_coal_generator.energy_stored"));
        energyPerTick = tag.getInt("compact_coal_generator.energy_per_tick");
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
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, HolderLookup.@NotNull Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }
}
