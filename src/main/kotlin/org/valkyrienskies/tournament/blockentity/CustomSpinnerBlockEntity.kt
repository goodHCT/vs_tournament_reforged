package org.valkyrienskies.tournament.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.tournament.registry.TournamentBlockEntities

class CustomSpinnerBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(TournamentBlockEntities.CUSTOM_SPINNER.get(), pos, state) {

    var customTorque: Double = 1.0 
    private var pendingActivation = false 

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        
        tag.putDouble("CustomTorque", customTorque)
        tag.putBoolean("PendingActivation", pendingActivation)
    }

    override fun load(compound: CompoundTag) {
        super.load(compound)
        
        if (compound.contains("CustomTorque")) {
            customTorque = compound.getDouble("CustomTorque")
        }
        if (compound.contains("PendingActivation")) {
            pendingActivation = compound.getBoolean("PendingActivation")
        }
    }

    fun setPendingActivation(active: Boolean) {
        pendingActivation = active
    }
    
    fun getPendingActivation(): Boolean {
        return pendingActivation
    }

    override fun getUpdateTag(): CompoundTag {
        val tag = super.getUpdateTag()
        
        tag.putDouble("CustomTorque", customTorque)
        tag.putBoolean("PendingActivation", pendingActivation)
        return tag
    }

    override fun handleUpdateTag(tag: CompoundTag) {
        super.handleUpdateTag(tag)
        
        if (tag.contains("CustomTorque")) {
            customTorque = tag.getDouble("CustomTorque")
        }
        if (tag.contains("PendingActivation")) {
            pendingActivation = tag.getBoolean("PendingActivation")
        }
    }

    companion object {
        fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: CustomSpinnerBlockEntity) {
            
            if (blockEntity.pendingActivation && level.hasNeighborSignal(pos)) {
                
                blockEntity.pendingActivation = false
            }
        }
    }
}