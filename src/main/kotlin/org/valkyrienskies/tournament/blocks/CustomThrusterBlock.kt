package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraftforge.network.NetworkHooks
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getLoadedShipManagingPos
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.tournament.TournamentItems
import org.valkyrienskies.tournament.TournamentProperties
import org.valkyrienskies.tournament.blockentity.CustomThrusterBlockEntity
import org.valkyrienskies.tournament.ship.TournamentShips
import org.valkyrienskies.tournament.util.DirectionalShape
import org.valkyrienskies.tournament.util.RotShapes
import org.valkyrienskies.tournament.util.extension.toBlock
import org.valkyrienskies.tournament.util.helper.Helper3d
import org.valkyrienskies.tournament.util.block.DirectionalBaseEntityBlock

class CustomThrusterBlock : DirectionalBaseEntityBlock(
    Properties.of()
        .mapColor(MapColor.STONE)
        .sound(SoundType.STONE)
        .strength(1.0f, 2.0f)
) {

    private val SHAPE = RotShapes.box(3.0, 5.0, 4.0, 13.0, 11.0, 16.0)

    private val CUSTOM_THRUSTER_SHAPE = DirectionalShape.south(SHAPE)

    init {
        registerDefaultState(defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(BlockStateProperties.POWER, 0)
            .setValue(TournamentProperties.TIER, 1)
        )
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return CUSTOM_THRUSTER_SHAPE[state.getValue(BlockStateProperties.FACING)]
    }

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

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level !is ServerLevel) return

        val signal = level.getBestNeighborSignal(pos)
        
        
        level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWER, signal))
        
        
        if (signal > 0) {
            updateThrusterState(level, pos, state)
        }
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (level !is ServerLevel) return

        
        disableThruster(level, pos)

        super.onRemove(state, level, pos, newState, isMoving)
    }

    override fun getDrops(state: BlockState, builder: LootParams.Builder): MutableList<ItemStack> {
        val drops = super.getDrops(state, builder)

        val tier = state.getValue(TournamentProperties.TIER)
        if (tier > 1) {
            drops.add(ItemStack(TournamentItems.UPGRADE_THRUSTER.get(), tier - 1))
        }

        return drops
    }

    private fun getShipControl(level: ServerLevel, pos: BlockPos)  =
        (level.getLoadedShipManagingPos(pos)
            ?.let { TournamentShips.getOrCreate(it) })

    
    private fun updateThrusterState(level: ServerLevel, pos: BlockPos, state: BlockState) {
        val signal = state.getValue(BlockStateProperties.POWER)
        
        
        if (signal > 0) {
            enableThruster(level, pos, state)
        } else {
            disableThruster(level, pos)
        }
    }

    private fun enableThruster(level: ServerLevel, pos: BlockPos, state: BlockState) {
        val shipControl = getShipControl(level, pos)
        if (shipControl != null) {
            val blockEntity = level.getBlockEntity(pos) as? CustomThrusterBlockEntity
            val thrustMultiplier = blockEntity?.customForce ?: 1.0
            
            shipControl.addThruster(
                pos,
                thrustMultiplier, 
                state.getValue(FACING).normal.toJOMLD() 
                    .mul(state.getValue(BlockStateProperties.POWER).toDouble())
            )
        }
    }

    private fun disableThruster(level: ServerLevel, pos: BlockPos) {
        val shipControl = getShipControl(level, pos)
        shipControl?.stopThruster(pos)
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

        
        level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWER, signal))
        
        
        updateThrusterState(level, pos, state.setValue(BlockStateProperties.POWER, signal))
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState {
        var dir = ctx.nearestLookingDirection

        if(ctx.player != null && ctx.player!!.isShiftKeyDown)
            dir = dir.opposite

        return defaultBlockState()
            .setValue(BlockStateProperties.FACING, dir)
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, randomSource: RandomSource) {
        super.animateTick(state, level, pos, randomSource)

        val rp = Helper3d.getShipRenderPosition(level, pos.toJOMLD())
        if (level.isWaterAt(rp.toBlock())) {
            return
        }

        if (state.getValue(BlockStateProperties.POWER) > 0) {
            val dir = state.getValue(FACING)

            val x = rp.x + (0.5 * (dir.stepX + 1))
            val y = rp.y + (0.5 * (dir.stepY + 1))
            val z = rp.z + (0.5 * (dir.stepZ + 1))
            val speedX = dir.stepX * -0.4
            val speedY = dir.stepY * -0.4
            val speedZ = dir.stepZ * -0.4

            level.addParticle(net.minecraft.core.particles.ParticleTypes.FLAME, x, y, z, speedX, speedY, speedZ)
        }
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return CustomThrusterBlockEntity(pos, state)
    }
}