package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.mod.common.getLoadedShipManagingPos
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.tournament.TournamentProperties
import org.valkyrienskies.tournament.blockentity.CustomSpinnerBlockEntity
import org.valkyrienskies.tournament.ship.TournamentShips
import org.valkyrienskies.tournament.util.DirectionalShape
import org.valkyrienskies.tournament.util.RotShapes
import org.valkyrienskies.tournament.util.block.DirectionalBaseEntityBlock
import org.valkyrienskies.mod.common.util.toJOML

class CustomSpinnerBlock : DirectionalBaseEntityBlock(
    Properties.of()
        .mapColor(MapColor.STONE)
        .sound(SoundType.STONE)
        .strength(1.0f, 2.0f)
) {

    private val SHAPE = RotShapes.cube()

    private val CUSTOM_SPINNER_SHAPE = DirectionalShape.north(SHAPE)

    init {
        registerDefaultState(defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(BlockStateProperties.POWER, 0)
            .setValue(TournamentProperties.TIER, 1)
        )
    }

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = CUSTOM_SPINNER_SHAPE[state.getValue(BlockStateProperties.FACING)]

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        return InteractionResult.PASS
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
        builder.add(BlockStateProperties.POWER)
        builder.add(TournamentProperties.TIER)
        super.createBlockStateDefinition(builder)
    }

    private fun getShipControl(level: ServerLevel, pos: BlockPos) =
        (level.getLoadedShipManagingPos(pos)
            ?.let { TournamentShips.getOrCreate(it) })

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level !is ServerLevel) return

        val signal = level.getBestNeighborSignal(pos)
        level.setBlock(pos, state.setValue(BlockStateProperties.POWER, signal), 2)

        
        if (signal > 0) {
            updateSpinnerState(level, pos, state)
        }
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (level !is ServerLevel) return

        
        disableSpinner(level, pos)

        super.onRemove(state, level, pos, newState, isMoving)
    }

    
    private fun updateSpinnerState(level: ServerLevel, pos: BlockPos, state: BlockState) {
        val signal = state.getValue(BlockStateProperties.POWER)

        if (signal > 0) {
            enableSpinner(level, pos, state)
        } else {
            disableSpinner(level, pos)
        }
    }

    private fun enableSpinner(level: ServerLevel, pos: BlockPos, state: BlockState) {
        val shipControl = getShipControl(level, pos)
        if (shipControl != null) {
            val blockEntity = level.getBlockEntity(pos) as? CustomSpinnerBlockEntity
            val torqueMultiplier = blockEntity?.customTorque ?: 1.0
            
            
            shipControl.addSpinner(
                pos.toJOML(),
                state.getValue(FACING)
                    .opposite
                    .normal
                    .toJOMLD()
                    .mul(torqueMultiplier * state.getValue(BlockStateProperties.POWER).toDouble())
            )
        }
    }

    private fun disableSpinner(level: ServerLevel, pos: BlockPos) {
        val shipControl = getShipControl(level, pos)
        shipControl?.removeSpinner(pos.toJOML())
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving)

        if (level !is ServerLevel) return

        val signal = level.getBestNeighborSignal(pos)
        val prevSignal = state.getValue(BlockStateProperties.POWER)

        if (signal == prevSignal) return

        
        level.setBlock(pos, state.setValue(BlockStateProperties.POWER, signal), 2)

        
        updateSpinnerState(level, pos, state.setValue(BlockStateProperties.POWER, signal))
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        var dir = ctx.nearestLookingDirection

        if (ctx.player != null && ctx.player!!.isShiftKeyDown)
            dir = dir.opposite

        return defaultBlockState()
            .setValue(BlockStateProperties.FACING, dir)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return CustomSpinnerBlockEntity(pos, state)
    }
}