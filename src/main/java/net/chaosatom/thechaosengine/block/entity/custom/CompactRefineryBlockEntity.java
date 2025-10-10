package net.chaosatom.thechaosengine.block.entity.custom;

import net.chaosatom.thechaosengine.block.entity.ChaosEngineBlockEntities;
import net.chaosatom.thechaosengine.fluid.ChaosEngineFluids;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class CompactRefineryBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    private final ContainerData data;
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private int progress = 0;
    private int maxProgress;
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
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return null;
    }

    /* Worldly Container Methods */

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        return false;
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int i) {
        return null;
    }

    @Override
    public ItemStack removeItem(int i, int i1) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return null;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {

    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public void clearContent() {

    }
}
