package org.valkyrienskies.tournament.event

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraftforge.event.level.ChunkEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.valkyrienskies.mod.common.getLoadedShipManagingPos
import org.valkyrienskies.tournament.TournamentBlocks
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.ship.TournamentShips
import org.valkyrienskies.tournament.TournamentMod

@Mod.EventBusSubscriber(modid = TournamentMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object ChunkLoadEvent {

    @SubscribeEvent
    fun onChunkLoad(event: ChunkEvent.Load) {
        val level = event.level
        if (level !is ServerLevel) return
        
        val chunk = event.chunk
        
        for (sectionIndex in 0 until chunk.sections.size) {
            val section = chunk.sections[sectionIndex] ?: continue
            if (section.hasOnlyAir()) continue

            val minY = sectionIndex shl 4  
            val maxY = minY + 15  

            for (x in 0..15) {
                for (z in 0..15) {
                    for (y in minY..maxY) {
                        val blockPos = BlockPos(x + (chunk.pos.x shl 4), y, z + (chunk.pos.z shl 4))
                        val blockState = level.getBlockState(blockPos)
                        
                        
                        if (blockState.block == TournamentBlocks.BALLOON.get() ||
                            blockState.block == TournamentBlocks.POWERED_BALLOON.get()) {
                            
                            
                            val ship = level.getLoadedShipManagingPos(blockPos)
                            if (ship != null) {
                                val shipControl = TournamentShips.getOrCreate(ship)
                                
                                
                                shipControl.removeBalloon(blockPos)
                                
                                
                                if (blockState.block == TournamentBlocks.POWERED_BALLOON.get()) {
                                    
                                    val signal = level.getBestNeighborSignal(blockPos)
                                    if (signal > 0) {
                                        
                                        val power = signal.toDouble() * TournamentConfig.SERVER.poweredBalloonAnalogStrength
                                        shipControl.addBalloon(blockPos, power)
                                    } else {
                                        
                                        shipControl.removeBalloon(blockPos)
                                    }
                                } else {
                                    
                                    shipControl.addBalloon(blockPos, TournamentConfig.SERVER.balloonPower)
                                }
                            }
                            
                            
                            if (blockState.block == TournamentBlocks.POWERED_BALLOON.get()) {
                                
                                val ship = level.getLoadedShipManagingPos(blockPos)
                                if (ship != null) {
                                    val shipControl = TournamentShips.getOrCreate(ship)
                                    shipControl.removeBalloon(blockPos) 
                                }
                                
                                
                                level.updateNeighborsAt(blockPos, blockState.block)
                                
                                
                                val signal = level.getBestNeighborSignal(blockPos)
                                if (signal > 0) {
                                    val ship2 = level.getLoadedShipManagingPos(blockPos)
                                    if (ship2 != null) {
                                        val shipControl = TournamentShips.getOrCreate(ship2)
                                        val power = signal.toDouble() * TournamentConfig.SERVER.poweredBalloonAnalogStrength
                                        shipControl.addBalloon(blockPos, power)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

