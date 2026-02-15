package org.valkyrienskies.tournament.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.Block
import kotlin.math.abs
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.registry.TournamentBlockEntities

abstract class PropellerBlockEntity<T : BlockEntity>(
    type: net.minecraft.world.level.block.entity.BlockEntityType<T>,
    pos: BlockPos,
    state: BlockState,
    val maxSpeed: Double,
    val acceleration: Double,
) : BlockEntity(type, pos, state) {

    var speed = 0.0
    var rotation = 0.0
    var signal = 0

    fun tick(level: Level) {
        val newSpeed = when {
            signal > 0 -> minOf(maxSpeed, speed + acceleration)
            else -> maxOf(-maxSpeed, speed - acceleration * 2) 
        }

        speed = newSpeed
        rotation += speed
        
        if (level.isClientSide) { 
            setChanged()
        }
    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        
        tag.putDouble("speed", speed)
        tag.putDouble("rotation", rotation)
        tag.putInt("signal", signal)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)

        speed = tag.getDouble("speed")
        rotation = tag.getDouble("rotation")
        signal = tag.getInt("signal")
    }

    override fun getUpdateTag(): CompoundTag =
        CompoundTag().also {
            saveAdditional(it)
        }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? =
        ClientboundBlockEntityDataPacket.create(this)

    fun update() {
        level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL_IMMEDIATE)
    }

    
    fun onSignalChange(newSignal: Int) {
        if (signal != newSignal) {
            signal = newSignal
            if (level?.isClientSide == true) {
                setChanged() 
            }
        }
    }

    companion object {
        val ticker = BlockEntityTicker<PropellerBlockEntity<*>> { level, _, _, be ->
            be.tick(level)
        }
    }
}

class BigPropellerBlockEntity(
    pos: BlockPos,
    state: BlockState,
): PropellerBlockEntity<BigPropellerBlockEntity>(
    TournamentBlockEntities.PROPELLER_BIG.get(),
    pos,
    state,
    TournamentConfig.SERVER.propellerBigSpeed.toDouble(),  
    TournamentConfig.SERVER.propellerBigAccel.toDouble()   
) {
    companion object {
        
    }
}

class SmallPropellerBlockEntity(
    pos: BlockPos,
    state: BlockState,
): PropellerBlockEntity<SmallPropellerBlockEntity>(
    TournamentBlockEntities.PROPELLER_SMALL.get(),
    pos,
    state,
    TournamentConfig.SERVER.propellerSmallSpeed.toDouble(),
    TournamentConfig.SERVER.propellerSmallAccel.toDouble()
) {
    companion object {
        
    }
}