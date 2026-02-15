package org.valkyrienskies.tournament.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.tournament.registry.TournamentBlockEntities

class SeatEntity(pos: BlockPos, state: BlockState) : BlockEntity(TournamentBlockEntities.SEAT.get(), pos, state) {
    companion object {
        
    }
}