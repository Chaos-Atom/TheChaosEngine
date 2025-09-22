package net.chaosatom.thechaosengine.block.custom;

import com.mojang.serialization.MapCodec;
import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.ModBlocks;
import net.chaosatom.thechaosengine.block.entity.ModBlockEntities;
import net.chaosatom.thechaosengine.block.entity.custom.AtmosphericCondenserBlockEntity;
import net.chaosatom.thechaosengine.block.entity.custom.CompactPulverizerBlockEntity;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AtmosphericCondenserBlock extends BaseEntityBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty DEPLOYED = BooleanProperty.create("deployed");

    public static final VoxelShape SHAPE_UNDEPLOYED = Block.box(0,0,0,16,11,16);
    public static final VoxelShape SHAPE_BASE = Block.box(0,0,0,16,16,16);
    public static final VoxelShape SHAPE_TOWER_MAIN = Block.box(2,16,2,14,30,14);
    public static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE, SHAPE_TOWER_MAIN).optimize();
    public static final VoxelShape SHAPE_TOP = Block.box(1,30,1,15,32,15);
    public static final VoxelShape SHAPE_COLLISION = Shapes.or(SHAPE_COMMON, SHAPE_TOP).optimize();

    public static final MapCodec<AtmosphericCondenserBlock> CODEC = simpleCodec(AtmosphericCondenserBlock::new);

    public AtmosphericCondenserBlock(Properties properties) {
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

    /* Voxel Shape related */

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getValue(DEPLOYED)) {
            return SHAPE_COLLISION;
        } else {
            return SHAPE_UNDEPLOYED;
        }
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(DEPLOYED)) {
            return SHAPE_COLLISION;
        } else {
            return SHAPE_UNDEPLOYED;
        }

    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(DEPLOYED)) {
            return SHAPE_COLLISION;
        } else {
            return SHAPE_UNDEPLOYED;
        }
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getValue(DEPLOYED)) {
            return SHAPE_COLLISION;
        } else {
            return SHAPE_UNDEPLOYED;
        }
    }

    /* FACING */

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, DEPLOYED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();

        if (!context.getLevel().getBlockState(pos.above()).canBeReplaced(context)) {
            return null;
        } else {
            return defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection().getOpposite())
                    .setValue(LIT, false)
                    .setValue(DEPLOYED, false);
        }
    }

    /* BLOCK ENTITY */

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AtmosphericCondenserBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AtmosphericCondenserBlockEntity atmosphericCondenserBlockEntity) {
                atmosphericCondenserBlockEntity.drops();
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hitResult) {
        if (!state.getValue(DEPLOYED)) {

            if (level.getBlockState(pos.above()).isAir()) {
                level.setBlock(pos, state.setValue(DEPLOYED, true), 3);

                if (level.getBlockEntity(pos) instanceof AtmosphericCondenserBlockEntity atmosphericCondenserBlockEntity) {
                    atmosphericCondenserBlockEntity.startDeployment();
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            } else {
                return ItemInteractionResult.FAIL;
            }
        }

        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);

            if (state.getValue(DEPLOYED)) {
                if (entity instanceof AtmosphericCondenserBlockEntity atmosphericCondenserBlockEntity) {
                    ((ServerPlayer) player).openMenu(new SimpleMenuProvider(atmosphericCondenserBlockEntity,
                            Component.translatable("block.thechaosengine.atmospheric_condenser")), pos);
                } else {
                    throw new IllegalStateException("Our Container provider is missing!");
                }
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }

        return createTickerHelper(blockEntityType, ModBlockEntities.ATMOSPHERIC_CONDENSER_BE.get(),
                (level1, blockPos, blockState, atmosphericCondenserBlockEntity)
                        -> atmosphericCondenserBlockEntity.tick(level1, blockPos, blockState));
    }

    /* TOOLTIPS */

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(Component.translatable("block." + TheChaosEngine.MOD_ID + ".atmospheric_condenser.tooltip"));

        super.appendHoverText(stack, context, tooltip, tooltipFlag);
    }
}
