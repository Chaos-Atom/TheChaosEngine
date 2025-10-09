package net.chaosatom.thechaosengine.block.custom;

import com.mojang.serialization.MapCodec;
import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.entity.ChaosEngineBlockEntities;
import net.chaosatom.thechaosengine.block.entity.custom.DeployableSolarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DeployableSolarBlock extends BaseEntityBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty DEPLOYED = BooleanProperty.create("deployed");

    public static final VoxelShape SHAPE_UNDEPLOYED = Block.box(0,0,0,16,11,16);
    public static final VoxelShape SHAPE_DEPLOYED = Block.box(0,0,0,16,13,16);

    public static MapCodec<DeployableSolarBlock> CODEC = simpleCodec(DeployableSolarBlock::new);

    public DeployableSolarBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false)
                .setValue(DEPLOYED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    /* Voxel Shape Methods */

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(DEPLOYED) ? SHAPE_DEPLOYED : SHAPE_UNDEPLOYED;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(DEPLOYED) ? SHAPE_DEPLOYED : SHAPE_UNDEPLOYED;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(DEPLOYED) ? SHAPE_DEPLOYED : SHAPE_UNDEPLOYED;
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(DEPLOYED) ? SHAPE_DEPLOYED : SHAPE_UNDEPLOYED;
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    /* Facing Methods */

    @Override
    public @NotNull BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    /* BlockState Methods */

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, DEPLOYED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();

        if (!context.getLevel().getBlockState(pos.above()).canBeReplaced(context)) {
            Player player = context.getPlayer();

            if (player != null && !context.getLevel().isClientSide()) {
                player.displayClientMessage(Component.translatable("message.thechaosengine.solar_panel_no_room"), true);
            }
            return null;
        }
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(LIT, false)
                .setValue(DEPLOYED, false);
    }

    /* Block Entity Methods */

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DeployableSolarBlockEntity(blockPos, blockState);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                                       InteractionHand hand, BlockHitResult hitResult) {

        if (!state.getValue(DEPLOYED)) {

            // Checks if the block about the mixer is an air block so it's free to expand upwards.
            if (level.getBlockState(pos.above()).isAir()) {
                level.setBlock(pos, state.setValue(DEPLOYED, true), 3); // Sets state to Deployed

                if (level.getBlockEntity(pos) instanceof DeployableSolarBlockEntity blockEntity) {
                    blockEntity.startSolarPanelDeployment();

                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            } else {
                // If there is a solid block, do not change state / does not deploy mixer, send message to player
                if (!level.isClientSide()) {
                    player.displayClientMessage(Component.translatable("message.thechaosengine.solar_panel_obstructed"), true);
                }
                return ItemInteractionResult.FAIL;
            }
        }

        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);

            if (state.getValue(DEPLOYED)) {
                if (entity instanceof DeployableSolarBlockEntity deployableSolarBlockEntity) {
                    ((ServerPlayer) player).openMenu(new SimpleMenuProvider(deployableSolarBlockEntity,
                            Component.translatable("block.thechaosengine.deployable_solar")), pos);
                } else {
                    throw new IllegalStateException("Our Container provider is missing!");
                }
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        // Checks if the new block placed is above a deployed (activated) mixer
        if (neighborPos.equals(pos.above()) && state.getValue(DEPLOYED)) {

            // Checks if that new block is not an air block (is a solid block)
            if (!level.getBlockState(pos.above()).isAir()) {
                level.setBlock(pos, state.setValue(DEPLOYED, false), 3);

                if (level.getBlockEntity(pos) instanceof DeployableSolarBlockEntity blockEntity) {
                    blockEntity.startSolarPanelRetraction();
                }
            }
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }

        return createTickerHelper(blockEntityType, ChaosEngineBlockEntities.DEPLOYABLE_SOLAR_BE.get(),
                (level1, blockPos, blockState, deployableSolarBlockEntity)
                        -> deployableSolarBlockEntity.tick(level1, blockPos, blockState));
    }

    /* Tooltips */
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(Component.translatable("block." + TheChaosEngine.MOD_ID + ".deployable_machine.tooltip"));

        super.appendHoverText(stack, context, tooltip, tooltipFlag);
    }

    static {
        // TODO: Add Shape Voxel setup
    }
}
