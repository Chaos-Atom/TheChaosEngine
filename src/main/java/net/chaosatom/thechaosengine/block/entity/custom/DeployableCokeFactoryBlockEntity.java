package net.chaosatom.thechaosengine.block.entity.custom;

import net.chaosatom.thechaosengine.block.entity.ChaosEngineBlockEntities;
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
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DeployableCokeFactoryBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider, WorldlyContainer {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Labeled Slot Indices
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    // Inventory & Processing Data Related
    private final ContainerData data;
    private int progress = 0;
    private int maxProgress;
    private int heatBonus = 1;
    private int maxHeatBonus = 10;

    public DeployableCokeFactoryBlockEntity(BlockPos pos, BlockState state) {
        super(ChaosEngineBlockEntities.DEPLOYABLE_COKE_FACTORY_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> DeployableCokeFactoryBlockEntity.this.progress;
                    case 1 -> DeployableCokeFactoryBlockEntity.this.maxProgress;
                    case 2 -> DeployableCokeFactoryBlockEntity.this.heatBonus;
                    case 3 -> DeployableCokeFactoryBlockEntity.this.maxHeatBonus;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: DeployableCokeFactoryBlockEntity.this.heatBonus = value; break;
                    case 1: DeployableCokeFactoryBlockEntity.this.maxHeatBonus = value; break;
                }
            }

            @Override
            public int getCount() { return 4; }
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
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(),3);
            }
        };
    }

    public IEnergyStorage getEnergyStorage(Direction direction) {
        return this.ENERGY_STORAGE;
    }

    /* GeckoLib Animation Methods */
    private static final RawAnimation DEPLOY_SEQUENCE = RawAnimation.begin()
            .thenPlay("coking_factory.deploying")
            .thenLoop("coking_factory.idle");
    private static final RawAnimation DEPLOYED_LOOP = RawAnimation.begin()
            .thenLoop("coking_factory.idle");
    private static final RawAnimation WORKING_LOOP = RawAnimation.begin()
            .thenLoop("coking_factory.working");
    private static final RawAnimation RETRACT_SEQUENCE = RawAnimation.begin()
            .thenPlay("coking_factory.retracting")
            .thenLoop("coking_factory.undeployed");
    private static final RawAnimation UNDEPLOYED_LOOP = RawAnimation.begin()
            .thenLoop("coking_factory.undeployed");

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
    public void startCokeFactoryDeployment() {
        if (this.animState == AnimationState.UNDEPLOYED) {
            this.animState = AnimationState.DEPLOYING;
            this.deployTime = DEPLOY_ANIMATION_LENGTH;
            setChanged();
        }
    }

    public void startCokeFactoryRetraction() {
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
        tag.putInt("deployable_coke_factory.energy", ENERGY_STORAGE.getEnergyStored());

        tag.putInt("deployable_coke_factory.animation_state", this.animState.ordinal());
        tag.putInt("deployable_coke_factory.deploy_time", this.deployTime);
        tag.putInt("deployable_coke_factory.retract_time", this.retractTime);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        ENERGY_STORAGE.setEnergy(tag.getInt("deployable_coke_factory.energy"));

        this.animState = AnimationState.values()[tag.getInt("deployable_coke_factory.animation_state")];
        this.deployTime = tag.getInt("deployable_coke_factory.deploy_time");
        this.retractTime = tag.getInt("deployable_coke_factory.retract_time");

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
        return Component.translatable("blockentity.thechaosengine.deployable_coke_factory");
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
}
