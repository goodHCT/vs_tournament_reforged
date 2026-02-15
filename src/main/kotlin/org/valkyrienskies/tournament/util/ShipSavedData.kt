package org.valkyrienskies.tournament.util

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.tournament.TournamentMod

class ShipSavedData : SavedData() {
    
    override fun save(tag: CompoundTag): CompoundTag {
        
        return tag
    }
    companion object {
        private const val DATA_NAME = "vs_tournament_ship_data"
        
        fun get(): ShipSavedData {
            
            TODO("Implement ShipSavedData.get()")
        }
        
        fun getFor(ship: ServerShip): ShipSavedData? {
            
            return null 
        }
        
        fun register() {
            
            TODO("Implement ShipSavedData.register()")
        }
    }
}