package org.valkyrienskies.tournament.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.tournament.registry.TournamentBlockEntities

class FlapBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(TournamentBlockEntities.FLAP.get(), pos, state) {

    
    var wingPower: Double = 150.0        
        set(value) {
            field = value
            notifyPhysicsUpdate()
        }
    var wingDrag: Double = 150.0         
        set(value) {
            field = value
            notifyPhysicsUpdate()
        }
    var wingBreakingForce: Double = 10.0 
        set(value) {
            field = value
            notifyPhysicsUpdate()
        }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        
        tag.putDouble("WingPower", wingPower)
        tag.putDouble("WingDrag", wingDrag)
        tag.putDouble("WingBreakingForce", wingBreakingForce)
    }

    override fun load(compound: CompoundTag) {
        super.load(compound)
        
        if (compound.contains("WingPower")) {
            wingPower = compound.getDouble("WingPower")
        }
        if (compound.contains("WingDrag")) {
            wingDrag = compound.getDouble("WingDrag")
        }
        if (compound.contains("WingBreakingForce")) {
            wingBreakingForce = compound.getDouble("WingBreakingForce")
        }
    }

    
    private fun notifyPhysicsUpdate() {
        val currentLevel = level
        if (currentLevel != null && !currentLevel.isClientSide) {
            
            val currentState = currentLevel.getBlockState(worldPosition)
            currentLevel.sendBlockUpdated(worldPosition, currentState, currentState, 2)
            
            setChanged()
        }
    }

    override fun getUpdateTag(): CompoundTag {
        val tag = super.getUpdateTag()
        
        tag.putDouble("WingPower", wingPower)
        tag.putDouble("WingDrag", wingDrag)
        tag.putDouble("WingBreakingForce", wingBreakingForce)
        return tag
    }

    override fun handleUpdateTag(tag: CompoundTag) {
        super.handleUpdateTag(tag)
        
        if (tag.contains("WingPower")) {
            wingPower = tag.getDouble("WingPower")
        }
        if (tag.contains("WingDrag")) {
            wingDrag = tag.getDouble("WingDrag")
        }
        if (tag.contains("WingBreakingForce")) {
            wingBreakingForce = tag.getDouble("WingBreakingForce")
        }
    }

    companion object {
        fun tick(level: net.minecraft.world.level.Level, pos: BlockPos, state: BlockState, blockEntity: FlapBlockEntity) {
            
        }
    }
}