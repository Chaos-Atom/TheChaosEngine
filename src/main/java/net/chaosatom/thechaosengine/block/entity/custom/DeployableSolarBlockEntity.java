package net.chaosatom.thechaosengine.block.entity.custom;

import net.chaosatom.thechaosengine.block.entity.ChaosEngineBlockEntities;
import net.chaosatom.thechaosengine.screen.custom.DeployableSolarMenu;
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
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class DeployableSolarBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ContainerData data;
    private static final int MAX_ENERGY_TRANSFER = 320;
    private int solarOutput = 0;
    private int maxSolarOutput = 65; // FE per tick
    private int solarOutputCooldown = 0;
    private static final int SOLAR_UPDATE_INTERVAL = 20;
    private int operationStatus = 0;

    public DeployableSolarBlockEntity(BlockPos pos, BlockState blockState) {
        super(ChaosEngineBlockEntities.DEPLOYABLE_SOLAR_BE.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> DeployableSolarBlockEntity.this.solarOutput;
                    case 1 -> DeployableSolarBlockEntity.this.maxSolarOutput;
                    case 2 -> DeployableSolarBlockEntity.this.operationStatus;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: DeployableSolarBlockEntity.this.solarOutput = value;
                    case 1: DeployableSolarBlockEntity.this.maxSolarOutput = value;
                    case 2: DeployableSolarBlockEntity.this.operationStatus = value;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    /* Capabilities */
    private final EnergyStorage ENERGY_STORAGE = createEnergyStorage();
    private EnergyStorage createEnergyStorage() {
        return new EnergyStorage(80000, MAX_ENERGY_TRANSFER) {
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

    /* GeckoLib Animation Setup */
    // Main Body Animations
    private static final RawAnimation DEPLOY_ANIM = RawAnimation.begin()
            .thenPlay("deployable_solar.deploying");
    private static final RawAnimation ACTIVE_LOOP = RawAnimation.begin()
            .thenPlay("deployable_solar.active");
    private static final RawAnimation RETRACT_ANIM = RawAnimation.begin()
            .thenPlay("deployable_solar.retracting");
    private static final RawAnimation UNDEPLOYED_LOOP = RawAnimation.begin()
            .thenPlay("deployable_solar.undeployed");

    private enum BodyAnimationState {
        DEPLOYING,
        ACTIVE,
        RETRACTING,
        UNDEPLOYED
    }

    private BodyAnimationState animState = BodyAnimationState.UNDEPLOYED;
    private int deployTime = 0;
    private static final int DEPLOY_ANIMATION_LENGTH = 80; // 4 seconds
    private int retractTime = 0;
    private static final int RETRACTING_ANIMATION_LENGTH = 35;

    // Animation Related Helper Methods
    public void startSolarPanelDeployment() {
        if (this.animState == BodyAnimationState.UNDEPLOYED) {
            this.animState = BodyAnimationState.DEPLOYING;
            this.deployTime = DEPLOY_ANIMATION_LENGTH;
            setChanged();
        }
    }

    public void startSolarPanelRetraction() {
        boolean isActive = this.animState == BodyAnimationState.ACTIVE;
        if (isActive) {
            this.animState = BodyAnimationState.RETRACTING;
            this.retractTime = RETRACTING_ANIMATION_LENGTH;
            setChanged();
        }
    }

    public float getSunFacingAngle() {
        Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        switch (facing) {
            case NORTH -> {
                return (float) -90;
            }
            case EAST -> {
                return (float) -180;
            }
            case SOUTH -> {
                return (float) 90;
            }
            case WEST -> {
                return (float) 0;
            }
        }
        return 0;
    }

    /* Main Machine Logic */
    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        boolean isWorking;
        if (this.animState == BodyAnimationState.DEPLOYING) {
            this.deployTime--;

            if (this.deployTime <= 0) {
                this.animState = BodyAnimationState.ACTIVE;
                setChanged();
            }
        } else if (this.animState == BodyAnimationState.RETRACTING) {
            this.retractTime--;

            if (this.retractTime <= 0) {
                this.animState = BodyAnimationState.UNDEPLOYED;
                setChanged();
            }
        }

        if (level.isClientSide()) {
            return;
        }

        if (this.solarOutputCooldown <= 0) {
            this.solarOutputCooldown = SOLAR_UPDATE_INTERVAL;
            calculateSolarOutput(level, blockPos);
            getOperationStatus(level, blockPos);
        } else {
            this.solarOutputCooldown--;
        }

        if (this.animState == BodyAnimationState.ACTIVE) {
            if (level.isDay() && level.canSeeSky(blockPos.above())) {
                isWorking = true;
                this.ENERGY_STORAGE.receiveEnergy(solarOutput, false);
                level.setBlock(blockPos, blockState.setValue(BlockStateProperties.LIT, isWorking), 3);
            } else {
                isWorking = false;
            }
        }
        pushEnergyToOutputs(level);
    }

    /* Machine Logic Helper Methods */

    private void pushEnergyToOutputs(Level level) {
        if (this.ENERGY_STORAGE.getEnergyStored() <= 0) { return; } // If solar generator has no energy, leave method

        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction[] outputSides = {facing.getOpposite(), Direction.DOWN};
        List<IEnergyStorage> validEnergyNeighbors = new ArrayList<>();

        for (Direction side : outputSides) {
            BlockPos neighborPos = worldPosition.relative(side);
            IEnergyStorage neighborEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, side.getOpposite());

            if (neighborEnergy != null) {
                if (neighborEnergy.canReceive()) {
                    validEnergyNeighbors.add(neighborEnergy);
                }
            }
        }

        if (!validEnergyNeighbors.isEmpty()) {
            int amountEnergyToPush = Math.min(this.ENERGY_STORAGE.getEnergyStored(), MAX_ENERGY_TRANSFER);
            int amountPerNeighbor = amountEnergyToPush / validEnergyNeighbors.size();

            if (amountPerNeighbor <= 0) {return;}

            for (IEnergyStorage neighbor : validEnergyNeighbors) {
                int energyAccepted = neighbor.receiveEnergy(amountPerNeighbor, false);
                if (energyAccepted > 0) {
                    this.ENERGY_STORAGE.extractEnergy(energyAccepted, false);
                    setChanged();
                }
            }
        }
    }

    private void getOperationStatus(Level level, BlockPos pos) {
        if (!level.canSeeSky(pos.above())) {
            this.operationStatus = 3;
        } else {
            if (level.isThundering()) {
                this.operationStatus = 3;
            } else if (level.isRaining()) {
                this.operationStatus = 2;
            } else if (!level.isDay()) {
                this.operationStatus = 1;
            } else {
                this.operationStatus = 0;
            }
        }
    }

    private void calculateSolarOutput(Level level, BlockPos pos) {
        long scaledDayTime = level.getDayTime() / 100;
        // Function to mimic real world solar panel power generation changes throughout the day
        this.solarOutput = (int) Math.min((long) ((-0.018)*(Math.pow((double) (scaledDayTime - 60), 2)) + this.maxSolarOutput), this.maxSolarOutput);
        if (level.isRaining()) {
            this.solarOutput = (int) ((this.solarOutput) * 0.25);
        }
        if (!level.canSeeSky(pos.above()) || level.isThundering()) {
            this.solarOutput = 0;
        }
    }

    /* General Block Entity Methods */

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("deployable_solar.energy", ENERGY_STORAGE.getEnergyStored());

        tag.putInt("deployable_solar.body_animation_state", this.animState.ordinal());

        tag.putInt("deployable_solar.deploy_time", this.deployTime);
        tag.putInt("deployable_solar.retract_time", this.retractTime);
        tag.putInt("deployable_solar.solar_output_cooldown", this.solarOutputCooldown);
        tag.putInt("deployable_solar.operation_status", this.operationStatus);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        ENERGY_STORAGE.setEnergy(tag.getInt("deployable_solar.energy"));

        this.animState = BodyAnimationState.values()[tag.getInt("deployable_solar.body_animation_state")];

        this.deployTime = tag.getInt("deployable_solar.deploy_time");
        this.retractTime = tag.getInt("deployable_solar.retract_time");
        this.solarOutputCooldown = tag.getInt("deployable_solar.solar_output_cooldown");
        this.operationStatus = tag.getInt("deployable_solar.operation_status");

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
        return Component.translatable("blockentity.thechaosengine.deployable_solar");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new DeployableSolarMenu(containerId, playerInventory, this, this.data);
    }

    /* GeckoLib Animatable Block Entity Methods */

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Deployment/Idle/Retraction", 0, state ->
                switch (state.getAnimatable().animState) {
                    case DEPLOYING -> state.setAndContinue(DEPLOY_ANIM);
                    case ACTIVE -> state.setAndContinue(ACTIVE_LOOP);
                    case RETRACTING -> state.setAndContinue(RETRACT_ANIM);
                    case UNDEPLOYED -> state.setAndContinue(UNDEPLOYED_LOOP);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
