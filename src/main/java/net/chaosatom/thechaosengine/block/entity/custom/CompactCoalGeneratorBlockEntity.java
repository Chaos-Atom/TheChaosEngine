package net.chaosatom.thechaosengine.block.entity.custom;

import net.chaosatom.thechaosengine.block.entity.ModBlockEntities;
import net.chaosatom.thechaosengine.block.entity.energy.ModEnergyStorage;
import net.chaosatom.thechaosengine.block.entity.energy.ModEnergyUtil;
import net.chaosatom.thechaosengine.screen.custom.CompactCoalGeneratorMenu;
import net.chaosatom.thechaosengine.util.ModTags;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class CompactCoalGeneratorBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private static final int INPUT_SLOT = 0;

    protected final ContainerData data;
    private int burnProgress = 160; // in ticks
    private int maxBurnProgress = 160; // ticks
    private boolean isBurning = false;

    private static final int ENERGY_TRANSFER_AMOUNT = 320;

    private final ModEnergyStorage ENERGY_STORAGE = createEnergyStorage();
    private ModEnergyStorage createEnergyStorage() {
        return new ModEnergyStorage(64000, ENERGY_TRANSFER_AMOUNT) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        };
    }

    public CompactCoalGeneratorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.COAL_GENERATOR_BE.get(), pPos, pBlockState);
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
    public Component getDisplayName() {
        return Component.literal("Compact Coal Generator");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CompactCoalGeneratorMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (hasFuelItemInSlot()) {
            if (!isBurningFuel()) {
                startBurning();
            }
        }

        if (isBurningFuel()) {
            if (getBlockState().getValue(BlockStateProperties.POWERED) != burnProgress > 0) {
                level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(BlockStateProperties.POWERED, burnProgress > 0));
            }
            increaseBurnProgress();
            if (currentFuelDoneBurning()) {
                level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(BlockStateProperties.POWERED, false));
                resetBurning();
            }
            fillUpOnEnergy();
        }
        pushEnergyToNeighbourAbove();
    }


    private void pushEnergyToNeighbourAbove() {
        if (ModEnergyUtil.doesBlockHaveEnergyStorage(this.worldPosition.above(), this.level)) {
            ModEnergyUtil.moveEnergy(this.worldPosition, this.worldPosition.above(), 320, this.level);
        }
    }

    private boolean hasFuelItemInSlot() {
        return this.itemHandler.getStackInSlot(INPUT_SLOT).is(ModTags.Items.COAL_GENERATOR_FUEL);
    }

    private boolean isBurningFuel() {
        return isBurning;
    }

    private void startBurning() {
        this.itemHandler.extractItem(INPUT_SLOT, 1, false);
        isBurning = true;
    }

    private void increaseBurnProgress() {
        this.burnProgress--; // Little misleading but decreases from a starting value towards zero
    }

    private boolean currentFuelDoneBurning() {
        return this.burnProgress <= 0;
    }

    private void resetBurning() {
        isBurning = false;
        this.burnProgress = 160;
    }

    private void fillUpOnEnergy() {
        this.ENERGY_STORAGE.receiveEnergy(320, false);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("compact_coal_generator.inventory", itemHandler.serializeNBT(registries));
        tag.putInt("compact_coal_generator.burn_progress", burnProgress);
        tag.putInt("compact_coal_generator.max_burn_progress", maxBurnProgress);

        tag.putInt("compact_coal_generator", ENERGY_STORAGE.getEnergyStored());

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        itemHandler.deserializeNBT(registries, tag.getCompound("compact_coal_generator.inventory"));
        ENERGY_STORAGE.setEnergy(tag.getInt("compact_coal_generator"));

        burnProgress = tag.getInt("compact_coal_generator.burn_progress");
        maxBurnProgress = tag.getInt("compact_coal_generator.max_burn_progress");
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }
}
