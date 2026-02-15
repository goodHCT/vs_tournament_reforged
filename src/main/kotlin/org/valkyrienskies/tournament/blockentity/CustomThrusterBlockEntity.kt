package org.valkyrienskies.tournament.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.tournament.registry.TournamentBlockEntities

class CustomThrusterBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(TournamentBlockEntities.CUSTOM_THRUSTER.get(), pos, state) {

    var customForce: Double = 1.0 
    private var pendingActivation = false 
    
    
    private var wasActive = false

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        
        tag.putDouble("CustomForce", customForce)
        tag.putBoolean("PendingActivation", pendingActivation)
        tag.putBoolean("WasActive", wasActive) 
    }

    override fun load(compound: CompoundTag) {
        super.load(compound)
        
        if (compound.contains("CustomForce")) {
            customForce = compound.getDouble("CustomForce")
        }
        if (compound.contains("PendingActivation")) {
            pendingActivation = compound.getBoolean("PendingActivation")
        }
        if (compound.contains("WasActive")) {
            wasActive = compound.getBoolean("WasActive")
        }
    }

    fun setPendingActivation(active: Boolean) {
        pendingActivation = active
    }
    
    fun getPendingActivation(): Boolean {
        return pendingActivation
    }
    
    
    fun getWasActive(): Boolean {
        return wasActive
    }
    
    
    fun setWasActive(active: Boolean) {
        wasActive = active
    }

    override fun getUpdateTag(): CompoundTag {
        val tag = super.getUpdateTag()
        
        tag.putDouble("CustomForce", customForce)
        tag.putBoolean("PendingActivation", pendingActivation)
        tag.putBoolean("WasActive", wasActive)
        return tag
    }

    override fun handleUpdateTag(tag: CompoundTag) {
        super.handleUpdateTag(tag)
        
        if (tag.contains("CustomForce")) {
            customForce = tag.getDouble("CustomForce")
        }
        if (tag.contains("PendingActivation")) {
            pendingActivation = tag.getBoolean("PendingActivation")
        }
        if (tag.contains("WasActive")) {
            wasActive = tag.getBoolean("WasActive")
        }
    }

    companion object {
        fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: CustomThrusterBlockEntity) {
            
            if (blockEntity.pendingActivation && level.hasNeighborSignal(pos)) {
                
                blockEntity.pendingActivation = false
            }
        }
    }
}