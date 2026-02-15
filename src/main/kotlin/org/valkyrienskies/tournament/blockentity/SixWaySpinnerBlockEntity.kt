package org.valkyrienskies.tournament.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.tournament.registry.TournamentBlockEntities
import org.valkyrienskies.mod.common.getLoadedShipManagingPos
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.ship.TournamentShips
import net.minecraft.world.level.LevelAccessor
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.mod.common.util.toJOML 

class SixWaySpinnerBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(TournamentBlockEntities.SIX_WAY_SPINNER.get(), pos, state) {

    
    private val torqueMultipliers = mutableMapOf(
        Direction.NORTH to 1.0,
        Direction.EAST to 1.0,
        Direction.SOUTH to 1.0,
        Direction.WEST to 1.0,
        Direction.UP to 1.0,
        Direction.DOWN to 1.0
    )

    
    var globalMultiplier: Double = 1.0

    
    private val lastSignalStrengths = mutableMapOf(
        Direction.NORTH to 0,
        Direction.EAST to 0,
        Direction.SOUTH to 0,
        Direction.WEST to 0,
        Direction.UP to 0,
        Direction.DOWN to 0
    )

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        
        
        for (direction in Direction.values()) {
            tag.putDouble("torque_multiplier_${direction.name.lowercase()}", torqueMultipliers[direction] ?: 1.0)
        }
        
        
        tag.putDouble("global_multiplier", globalMultiplier)
        
        
        for (direction in Direction.values()) {
            tag.putInt("last_signal_${direction.name.lowercase()}", lastSignalStrengths[direction] ?: 0)
        }
    }

    override fun load(compound: CompoundTag) {
        super.load(compound)
        
        
        for (direction in Direction.values()) {
            val key = "torque_multiplier_${direction.name.lowercase()}"
            if (compound.contains(key)) {
                torqueMultipliers[direction] = compound.getDouble(key)
            } else {
                torqueMultipliers[direction] = 1.0 
            }
        }
        
        
        if (compound.contains("global_multiplier")) {
            globalMultiplier = compound.getDouble("global_multiplier")
        } else {
            globalMultiplier = 1.0 
        }
        
        
        for (direction in Direction.values()) {
            val key = "last_signal_${direction.name.lowercase()}"
            if (compound.contains(key)) {
                lastSignalStrengths[direction] = compound.getInt(key)
            }
        }
    }

    
    fun getTorqueMultiplierForDirection(direction: Direction): Double {
        return torqueMultipliers[direction] ?: 1.0
    }

    
    fun setTorqueMultiplierForDirection(direction: Direction, multiplier: Double) {
        torqueMultipliers[direction] = multiplier
        setChanged() 
    }

    
    private fun getLastSignalStrength(direction: Direction): Int {
        return lastSignalStrengths[direction] ?: 0
    }
    
    
    private fun setLastSignalStrength(direction: Direction, strength: Int) {
        lastSignalStrengths[direction] = strength
    }

    override fun getUpdateTag(): CompoundTag {
        val tag = super.getUpdateTag()
        
        
        for (direction in Direction.values()) {
            tag.putDouble("torque_multiplier_${direction.name.lowercase()}", torqueMultipliers[direction] ?: 1.0)
        }
        
        
        tag.putDouble("global_multiplier", globalMultiplier)
        
        
        for (direction in Direction.values()) {
            tag.putInt("last_signal_${direction.name.lowercase()}", lastSignalStrengths[direction] ?: 0)
        }
        return tag
    }

    override fun handleUpdateTag(tag: CompoundTag) {
        super.handleUpdateTag(tag)
        
        
        for (direction in Direction.values()) {
            val key = "torque_multiplier_${direction.name.lowercase()}"
            if (tag.contains(key)) {
                torqueMultipliers[direction] = tag.getDouble(key)
            }
        }
        
        
        if (tag.contains("global_multiplier")) {
            globalMultiplier = tag.getDouble("global_multiplier")
        }
        
        
        for (direction in Direction.values()) {
            val key = "last_signal_${direction.name.lowercase()}"
            if (tag.contains(key)) {
                lastSignalStrengths[direction] = tag.getInt(key)
            }
        }
    }

    companion object {
        
        fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: SixWaySpinnerBlockEntity) {
            if (level.isClientSide) return
            
            val ship = level.getLoadedShipManagingPos(pos)
            if (ship != null) {
                
                val loadedServerShip = ship as? LoadedServerShip
                if (loadedServerShip != null) {
                    val shipControl = TournamentShips.getOrCreate(loadedServerShip)
                    
                    
                    val signals = mutableMapOf<Direction, Int>()
                    for (direction in Direction.values()) {
                        val signal = level.getSignal(pos.relative(direction), direction)
                        signals[direction] = signal
                    }
                    
                    
                    for (direction in Direction.values()) {
                        val currentSignal = signals[direction] ?: 0
                        val lastSignal = blockEntity.getLastSignalStrength(direction)
                        
                        
                        if (currentSignal != lastSignal) {
                            
                            if (lastSignal > 0) {
                                shipControl.removeSpinner(pos.toJOML(), getTorqueVectorForDirection(direction, blockEntity.getTorqueMultiplierForDirection(direction) * blockEntity.globalMultiplier * lastSignal.toDouble()))
                            }
                            
                            
                            if (currentSignal > 0) {
                                val torqueMultiplier = blockEntity.getTorqueMultiplierForDirection(direction) * blockEntity.globalMultiplier
                                val totalTorque = torqueMultiplier * currentSignal.toDouble()

                                shipControl.addSpinner(
                                    pos.toJOML(),  
                                    
                                    getTorqueVectorForDirection(direction, totalTorque)
                                )
                            }
                            
                            
                            blockEntity.setLastSignalStrength(direction, currentSignal)
                        }
                    }
                }
            }
        }
        
        
        private fun getTorqueVectorForDirection(direction: Direction, torqueValue: Double): Vector3d {
            return when (direction) {
                Direction.UP -> Vector3d(0.0, -torqueValue, 0.0) 
                Direction.DOWN -> Vector3d(0.0, torqueValue, 0.0) 
                Direction.NORTH -> Vector3d(0.0, 0.0, torqueValue) 
                Direction.SOUTH -> Vector3d(0.0, 0.0, -torqueValue) 
                Direction.EAST -> Vector3d(-torqueValue, 0.0, 0.0) 
                Direction.WEST -> Vector3d(torqueValue, 0.0, 0.0) 
            }
        }
    }
}