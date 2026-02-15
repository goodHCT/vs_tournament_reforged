package org.valkyrienskies.tournament.blockentity.explosive

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.tournament.registry.TournamentBlockEntities

class ExplosiveBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(TournamentBlockEntities.EXPLOSIVE_INSTANT_SMALL.get(), pos, state) {

    var explosionTicks = 0

    fun explode(level: Level, pos: BlockPos) {
        
        level.explode(
            null,
            pos.x.toDouble() + 0.5,
            pos.y.toDouble() + 0.5,
            pos.z.toDouble() + 0.5,
            6.0F, 
            Level.ExplosionInteraction.BLOCK 
        )
    }
    
    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        if (explosionTicks > 0) {
            explosionTicks--
            if (explosionTicks <= 0) {
                explode(level, pos)
            }
        }
    }

    companion object {
        
        
        fun tick(level: Level, pos: BlockPos, state: BlockState, be: BlockEntity) {
            be as ExplosiveBlockEntity
            be.tick(level, pos, state)
        }
    }
}