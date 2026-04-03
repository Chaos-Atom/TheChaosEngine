package net.chaosatom.thechaosengine.block.custom;

import com.mojang.serialization.MapCodec;
import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.entity.ChaosEngineBlockEntities;
import net.chaosatom.thechaosengine.block.entity.custom.DeployableCokeFactoryBlockEntity;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DeployableCokeFactoryBlock extends BaseEntityBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty DEPLOYED = BooleanProperty.create("deployed");

    public static MapCodec<DeployableCokeFactoryBlock> CODEC = simpleCodec(DeployableCokeFactoryBlock::new);

    public DeployableCokeFactoryBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false)
                .setValue(DEPLOYED, false));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    /* Facing Methods */

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
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
                player.displayClientMessage(Component.translatable("message.thechaosengine.coke_factory_no_room"), true);
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
        return new DeployableCokeFactoryBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof DeployableCokeFactoryBlockEntity deployableCokeFactoryBlockEntity) {
                deployableCokeFactoryBlockEntity.drops();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!state.getValue(DEPLOYED)) {
            if (level.getBlockState(pos.above()).isAir()) {
                level.setBlock(pos, state.setValue(DEPLOYED, true), 3);

                if (level.getBlockEntity(pos) instanceof DeployableCokeFactoryBlockEntity deployableCokeFactoryBlockEntity) {
                    deployableCokeFactoryBlockEntity.startCokeFactoryDeployment();
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            } else {
                if (!level.isClientSide()) {
                    player.displayClientMessage(Component.translatable("message.thechaosengine.coke_factory_obstructed"), true);
                }
                return ItemInteractionResult.FAIL;
            }
        }

        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (state.getValue(DEPLOYED)) {
                if (blockEntity instanceof DeployableCokeFactoryBlockEntity deployableCokeFactoryBlockEntity) {
                    ((ServerPlayer) player).openMenu(new SimpleMenuProvider(deployableCokeFactoryBlockEntity,
                            Component.translatable("block.thechaosengine.deployable_coke_factory")), pos);
                } else {
                    throw new IllegalStateException("Our Container provider is missing!");
                }
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (neighborPos.equals(pos.above()) && state.getValue(DEPLOYED)) {
            if (!level.getBlockState(pos.above()).isAir()) {
                level.setBlock(pos, state.setValue(DEPLOYED, false), 3);

                if (level.getBlockEntity(pos) instanceof DeployableCokeFactoryBlockEntity deployableCokeFactoryBlockEntity) {
                    deployableCokeFactoryBlockEntity.startCokeFactoryRetraction();
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

        return createTickerHelper(blockEntityType, ChaosEngineBlockEntities.DEPLOYABLE_COKE_FACTORY_BE.get(),
                (level1, blockPos, blockState, deployableCokeFactoryBlockEntity)
                        -> deployableCokeFactoryBlockEntity.tick(level1, blockPos, blockState));
    }

    /* Tooltips */
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(Component.translatable("block." + TheChaosEngine.MOD_ID + ".deployable_machine.tooltip"));

        super.appendHoverText(stack, context, tooltip, tooltipFlag);
    }
}