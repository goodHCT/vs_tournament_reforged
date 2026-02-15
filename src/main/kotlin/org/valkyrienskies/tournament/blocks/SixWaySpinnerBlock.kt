package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
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
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.TournamentProperties
import org.valkyrienskies.tournament.blockentity.SixWaySpinnerBlockEntity
import org.valkyrienskies.tournament.ship.TournamentShips
import org.valkyrienskies.tournament.util.RotShapes
import org.valkyrienskies.tournament.util.block.DirectionalBaseEntityBlock
import org.valkyrienskies.mod.common.util.toJOML

import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import org.valkyrienskies.tournament.registry.TournamentBlockEntities
import org.joml.Vector3d
import org.joml.Vector3i

class SixWaySpinnerBlock : DirectionalBaseEntityBlock(
    Properties.of()
        .mapColor(MapColor.STONE)
        .sound(SoundType.STONE)
        .strength(1.0f, 2.0f)
) {

    private val SHAPE = RotShapes.cube()



    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(TournamentProperties.TIER, 1)
        )
    }

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = SHAPE.makeMcShape()

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: net.minecraft.world.entity.player.Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        return InteractionResult.PASS
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(TournamentProperties.TIER)
        super.createBlockStateDefinition(builder)
    }

    private fun getShipControl(level: ServerLevel, pos: BlockPos) =
        (level.getLoadedShipManagingPos(pos)
            ?.let { TournamentShips.getOrCreate(it) })

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (level !is ServerLevel) return

        
        disableAllSpinners(level, pos)

        super.onRemove(state, level, pos, newState, isMoving)
    }



    private fun disableAllSpinners(level: ServerLevel, pos: BlockPos) {
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
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        return defaultBlockState()
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return SixWaySpinnerBlockEntity(pos, state)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (level.isClientSide) {
            null
        } else {
            createTickerHelper(type, TournamentBlockEntities.SIX_WAY_SPINNER.get()) { level, pos, state, blockEntity ->
                SixWaySpinnerBlockEntity.tick(level, pos, state, blockEntity)
            }
        }
    }
}