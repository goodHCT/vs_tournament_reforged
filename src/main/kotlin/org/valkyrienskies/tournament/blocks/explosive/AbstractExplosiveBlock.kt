package org.valkyrienskies.tournament.blocks.explosive

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.Explosion
import org.valkyrienskies.tournament.blockentity.explosive.ExplosiveBlockEntity

abstract class AbstractExplosiveBlock : BaseEntityBlock(
    Properties.of()
        .mapColor(MapColor.SAND)
        .sound(SoundType.GRAVEL)
        .strength(2.0f, 2.0f)  
) {

    init {
        
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.POWER, 0))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder.add(BlockStateProperties.POWER))
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

        val signal = level.getBestNeighborSignal(pos)
        if(signal > 0)
            ignite(level, pos)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = ExplosiveBlockEntity(pos, state)

    
    abstract fun explode(level: ServerLevel, pos: BlockPos)

    
    open fun explosionTicks() : Int { return 0 }

    
    open fun explodeTick(level: ServerLevel, pos: BlockPos) {}

    
    final fun ignite(level: ServerLevel, pos: BlockPos) {
        if(explosionTicks() > 0) {
            explodeTick(level, pos)
            try {
                (level.getBlockEntity(pos) as ExplosiveBlockEntity).explosionTicks = explosionTicks()
            } catch (_ : Exception) {}
        } else {
            
            level.destroyBlock(pos, false) 
            
            level.explode(
                null,
                pos.x.toDouble() + 0.5,
                pos.y.toDouble() + 0.5,
                pos.z.toDouble() + 0.5,
                getExplosionStrength(), 
                Level.ExplosionInteraction.BLOCK
            )
            explode(level, pos)
        }
    }

    
    open fun getExplosionStrength(): Float = 4.0F

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    final override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {

        return BlockEntityTicker { levelB: Level, posB: BlockPos, stateB: BlockState, t: T ->
            run {
                ExplosiveBlockEntity.tick(
                    levelB,
                    posB,
                    stateB,
                    t
                )
            }
        }
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level !is ServerLevel) return

        val signal = level.getBestNeighborSignal(pos)

        if (signal > 0) {
            ignite(level, pos)
        }
    }

    
    override fun wasExploded(level: Level, pos: BlockPos, explosion: Explosion) {
        if (level is ServerLevel) {
            
            ignite(level, pos)
        }
    }

}