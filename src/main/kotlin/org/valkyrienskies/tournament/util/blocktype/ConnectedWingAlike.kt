package org.valkyrienskies.tournament.util.blocktype

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.tournament.TournamentShapes


abstract class ConnectedWingAlike(properties: Properties) : Block(properties) {
    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(FACING, Direction.UP)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, NORTH, SOUTH, EAST, WEST, UP, DOWN)
        super.createBlockStateDefinition(builder)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val state = super.getStateForPlacement(context)
        return getNewState(
            state?.setValue(
                DirectionalBlock.FACING,
                context.nearestLookingDirection.opposite
            ), context.level, context.clickedPos
        )
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun rotate(state: BlockState, rot: Rotation): BlockState {
        return when (rot) {
            Rotation.COUNTERCLOCKWISE_90, Rotation.CLOCKWISE_90 -> when (state.getValue(FACING)) {
                Direction.NORTH -> state.setValue(FACING, Direction.EAST)
                Direction.EAST -> state.setValue(FACING, Direction.UP)
                Direction.UP -> state.setValue(FACING, Direction.NORTH)
                else -> state
            }

            else -> state
        }
    }

    abstract fun getNewState(state: BlockState?, level: Level?, pos: BlockPos?): BlockState?

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getShape(
        pState: BlockState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pContext: CollisionContext
    ): VoxelShape {
        return TournamentShapes.WING.get(
            when (pState.getValue<Direction>(FACING)) {
                Direction.EAST, Direction.WEST -> Direction.Axis.X
                Direction.UP, Direction.DOWN -> Direction.Axis.Y
                Direction.NORTH, Direction.SOUTH -> Direction.Axis.Z
            }
        )
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving)
        level.setBlockAndUpdate(pos, getNewState(state, level, pos))
    }

    companion object {
        val FACING = BlockStateProperties.FACING
        val NORTH = BlockStateProperties.NORTH
        val SOUTH = BlockStateProperties.SOUTH
        val EAST = BlockStateProperties.EAST
        val WEST = BlockStateProperties.WEST
        val UP = BlockStateProperties.UP
        val DOWN = BlockStateProperties.DOWN
    }
}