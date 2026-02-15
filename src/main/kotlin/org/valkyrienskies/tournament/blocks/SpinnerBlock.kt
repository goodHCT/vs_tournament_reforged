package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.mod.common.getLoadedShipManagingPos
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.tournament.ship.TournamentShips
import org.valkyrienskies.tournament.util.DirectionalShape
import org.valkyrienskies.tournament.util.RotShapes

class SpinnerBlock : DirectionalBlock(
    Properties.of()
        .mapColor(MapColor.STONE)
        .sound(SoundType.STONE)
        .strength(1.0f, 2.0f)
) {

    val SHAPE = RotShapes.cube()

    val SPINNER_SHAPE = DirectionalShape.north(SHAPE)

    init {
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.POWER, 0))
    }

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = SPINNER_SHAPE[state.getValue(BlockStateProperties.FACING)]

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
        builder.add(BlockStateProperties.POWER)
        super.createBlockStateDefinition(builder)
    }

    private fun getShipControl(level: ServerLevel, pos: BlockPos) =
        (level.getLoadedShipManagingPos(pos)
            ?.let { TournamentShips.getOrCreate(it) })

    
    private fun updateSpinnerState(level: ServerLevel, pos: BlockPos, state: BlockState) {
        val signal = level.getBestNeighborSignal(pos)
        val prevSignal = state.getValue(BlockStateProperties.POWER)
        
        
        if (signal == prevSignal) return
        
        
        level.setBlock(pos, state.setValue(BlockStateProperties.POWER, signal), 2)
        
        
        if (signal > 0) {
            enableSpinner(level, pos, state.setValue(BlockStateProperties.POWER, signal))
        } else {
            disableSpinner(level, pos, state.setValue(BlockStateProperties.POWER, signal))
        }
    }

    private fun enableSpinner(level: ServerLevel, pos: BlockPos, state: BlockState) {
        val shipControl = getShipControl(level, pos)
        shipControl?.addSpinner(
            pos.toJOML(),
            state.getValue(FACING)
                .opposite
                .normal
                .toJOMLD()
                .mul(state.getValue(BlockStateProperties.POWER).toDouble())
        )
    }

    private fun disableSpinner(level: ServerLevel, pos: BlockPos, state: BlockState) {
        val shipControl = getShipControl(level, pos)
        shipControl?.removeSpinner(pos.toJOML())
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        
        updateSpinnerState(level, pos, state)
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, level, pos, newState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        
        disableSpinner(level, pos, state)
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

        if (level as? ServerLevel == null) return

        
        updateSpinnerState(level, pos, state)
    }

    override fun getStateForPlacement(
        ctx: BlockPlaceContext
    ): BlockState = defaultBlockState()
            .setValue(FACING, ctx.nearestLookingDirection.opposite)

    override fun getBlockSupportShape(
        state: BlockState,
        reader: BlockGetter,
        pos: BlockPos
    ): VoxelShape = RotShapes.cube().makeMcShape()
}