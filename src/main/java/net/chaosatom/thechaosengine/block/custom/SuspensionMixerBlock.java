package net.chaosatom.thechaosengine.block.custom;

import com.mojang.serialization.MapCodec;
import net.chaosatom.thechaosengine.TheChaosEngine;
import net.chaosatom.thechaosengine.block.entity.ChaosEngineBlockEntities;
import net.chaosatom.thechaosengine.block.entity.custom.SuspensionMixerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
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

public class SuspensionMixerBlock extends BaseEntityBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty DEPLOYED = BooleanProperty.create("deployed");

    public static final VoxelShape SHAPE_UNDEPLOYED_BODY;
    public static final VoxelShape SHAPE_UNDEPLOYED_ARM_NORTH;
    public static final VoxelShape SHAPE_UNDEPLOYED_NORTH;
    public static final VoxelShape SHAPE_BASIN;
    public static final VoxelShape SHAPE_DEPLOYED_ARM_NORTH;
    public static final VoxelShape SHAPE_DEPLOYED_NORTH;
    public static final VoxelShape SHAPE_UNDEPLOYED_ARM_EAST;
    public static final VoxelShape SHAPE_UNDEPLOYED_EAST;
    public static final VoxelShape SHAPE_DEPLOYED_ARM_EAST;
    public static final VoxelShape SHAPE_DEPLOYED_EAST;
    public static final VoxelShape SHAPE_UNDEPLOYED_ARM_SOUTH;
    public static final VoxelShape SHAPE_UNDEPLOYED_SOUTH;
    public static final VoxelShape SHAPE_DEPLOYED_ARM_SOUTH;
    public static final VoxelShape SHAPE_DEPLOYED_SOUTH;
    public static final VoxelShape SHAPE_UNDEPLOYED_ARM_WEST;
    public static final VoxelShape SHAPE_UNDEPLOYED_WEST;
    public static final VoxelShape SHAPE_DEPLOYED_ARM_WEST;
    public static final VoxelShape SHAPE_DEPLOYED_WEST;

    public static MapCodec<SuspensionMixerBlock> CODEC = simpleCodec(SuspensionMixerBlock::new);

    public SuspensionMixerBlock(Properties properties) {
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
        if (state.getValue(DEPLOYED)) {
            return switch (state.getValue(FACING)) {
                case EAST -> SHAPE_DEPLOYED_EAST;
                case SOUTH -> SHAPE_DEPLOYED_SOUTH;
                case WEST -> SHAPE_DEPLOYED_WEST;
                default -> SHAPE_DEPLOYED_NORTH;
            };
        } else {
            return switch (state.getValue(FACING)) {
                case EAST -> SHAPE_UNDEPLOYED_EAST;
                case SOUTH -> SHAPE_UNDEPLOYED_SOUTH;
                case WEST -> SHAPE_UNDEPLOYED_WEST;
                default -> SHAPE_UNDEPLOYED_NORTH;
            };
        }
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getValue(DEPLOYED)) {
            return SHAPE_DEPLOYED_NORTH;
        } else {
            return SHAPE_UNDEPLOYED_NORTH;
        }
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (state.getValue(DEPLOYED)) {
            return SHAPE_DEPLOYED_NORTH;
        } else {
            return SHAPE_UNDEPLOYED_NORTH;
        }
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getValue(DEPLOYED)) {
            return SHAPE_DEPLOYED_NORTH;
        } else {
            return SHAPE_UNDEPLOYED_NORTH;
        }
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    /* Facing Methods */

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    /* BlockState Methods */

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING,LIT,DEPLOYED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();

        if (!context.getLevel().getBlockState(pos.above()).canBeReplaced(context)) {
            Player player = context.getPlayer();

            if (player != null && !context.getLevel().isClientSide()) {
                player.displayClientMessage(Component.translatable("message.thechaosengine.mixer_no_room"), true);
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
        return new SuspensionMixerBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SuspensionMixerBlockEntity suspensionMixerBlockEntity) {
                suspensionMixerBlockEntity.drops();
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hitResult) {
        if (!state.getValue(DEPLOYED)) {

            // Checks if the block about the mixer is an air block so it's free to expand upwards.
            if (level.getBlockState(pos.above()).isAir()) {
                level.setBlock(pos, state.setValue(DEPLOYED, true), 3); // Sets state to Deployed

                if (level.getBlockEntity(pos) instanceof SuspensionMixerBlockEntity blockEntity) {
                    blockEntity.startMixerDeployment();
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            } else {
                // If there is a solid block, do not change state / does not deploy mixer, send message to player
                if (!level.isClientSide()) {
                    player.displayClientMessage(Component.translatable("message.thechaosengine.mixer_obstructed"), true);
                }
                return ItemInteractionResult.FAIL;
            }
        }

        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);

            if (state.getValue(DEPLOYED)) {
                if (entity instanceof SuspensionMixerBlockEntity suspensionMixerBlockEntity) {
                    ((ServerPlayer) player).openMenu(new SimpleMenuProvider(suspensionMixerBlockEntity,
                            Component.translatable("block.thechaosengine.suspension_mixer")), pos);
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

                if (level.getBlockEntity(pos) instanceof SuspensionMixerBlockEntity blockEntity) {
                    blockEntity.startMixerRetraction();
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

        return createTickerHelper(blockEntityType, ChaosEngineBlockEntities.SUSPENSION_MIXER_BE.get(),
                (level1, blockPos, blockState, suspensionMixerBlockEntity)
                        -> suspensionMixerBlockEntity.tick(level1, blockPos, blockState));
    }

    /* TOOLTIPS */

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(Component.translatable("block." + TheChaosEngine.MOD_ID + ".deployable_machine.tooltip"));

        super.appendHoverText(stack, context, tooltip, tooltipFlag);
    }

    /* SOUNDS & PARTICLES */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }
        double xPos = (double)pos.getX() + (double)0.5F;
        double yPos = (double)pos.getY() + (double)1.05F;
        double zPos = (double)pos.getZ() + (double)0.5F;
        /* TODO: Add custom sounds to Suspension Mixer
        if (random.nextDouble() < 0.1) {
            // Plays sounds at a specific spot
            level.playLocalSound(xPos, yPos, zPos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS,
                    0.85F, 1F,
                    true);
        }
         */

        double spread = 0.54; // Higher values gives greater spread about origin
        double jump = 0.17; // Higher the value, the further the particles will spawn from origin
        double xOffset = random.nextDouble() * spread - 0.25;
        double yOffset =  Math.abs(random.nextDouble() * jump);
        double zOffset = random.nextDouble() * spread - 0.25;
        double wiggle = random.nextDouble() * 0.15; // Adds randomness to speed
        if (level.getBlockEntity(pos) instanceof SuspensionMixerBlockEntity suspensionMixerBlockEntity
                && !suspensionMixerBlockEntity.itemHandler.getStackInSlot(0).isEmpty()) {
            level.addParticle(ParticleTypes.SPLASH,
                    xPos + xOffset, yPos + yOffset, zPos + zOffset,
                    (double)0.0F, (double)0.01F, (double)0.0F);
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, suspensionMixerBlockEntity.itemHandler.getStackInSlot(0)),
                    xPos + xOffset, yPos + yOffset, zPos + zOffset,
                    (double)0.0F, (double)0.02F + wiggle, (double)0.0F);
        }
    }

    static {
        SHAPE_UNDEPLOYED_BODY = Block.box(0,0,0,16,9,16);
        SHAPE_BASIN = Block.box(0,0,0, 16,16,16); // Deployed main body

        // NORTH (Default) Direction
        SHAPE_UNDEPLOYED_ARM_NORTH = Block.box(6,9,0,10,14,11);
        SHAPE_UNDEPLOYED_NORTH = Shapes.or(SHAPE_UNDEPLOYED_BODY, SHAPE_UNDEPLOYED_ARM_NORTH).optimize();
        SHAPE_DEPLOYED_ARM_NORTH = Block.box(6,16,7, 10,32,15);
        SHAPE_DEPLOYED_NORTH = Shapes.or(SHAPE_BASIN, SHAPE_DEPLOYED_ARM_NORTH).optimize();

        // EAST
        SHAPE_UNDEPLOYED_ARM_EAST = Block.box(5, 9, 6, 16, 14, 10);
        SHAPE_UNDEPLOYED_EAST = Shapes.or(SHAPE_UNDEPLOYED_BODY, SHAPE_UNDEPLOYED_ARM_EAST).optimize();
        SHAPE_DEPLOYED_ARM_EAST = Block.box(1, 16, 6, 9, 32, 10);
        SHAPE_DEPLOYED_EAST = Shapes.or(SHAPE_BASIN, SHAPE_DEPLOYED_ARM_EAST).optimize();

        // SOUTH
        SHAPE_UNDEPLOYED_ARM_SOUTH = Block.box(6, 9, 5, 10, 14, 16);
        SHAPE_UNDEPLOYED_SOUTH = Shapes.or(SHAPE_UNDEPLOYED_BODY, SHAPE_UNDEPLOYED_ARM_SOUTH).optimize();
        SHAPE_DEPLOYED_ARM_SOUTH = Block.box(6, 16, 1, 10, 32, 9);
        SHAPE_DEPLOYED_SOUTH = Shapes.or(SHAPE_BASIN, SHAPE_DEPLOYED_ARM_SOUTH).optimize();

        // WEST
        SHAPE_UNDEPLOYED_ARM_WEST = Block.box(0, 9, 6, 11, 14, 10);
        SHAPE_UNDEPLOYED_WEST = Shapes.or(SHAPE_UNDEPLOYED_BODY, SHAPE_UNDEPLOYED_ARM_WEST).optimize();
        SHAPE_DEPLOYED_ARM_WEST = Block.box(7, 16, 6, 15, 32, 10);
        SHAPE_DEPLOYED_WEST = Shapes.or(SHAPE_BASIN, SHAPE_DEPLOYED_ARM_WEST).optimize();
    }
}
