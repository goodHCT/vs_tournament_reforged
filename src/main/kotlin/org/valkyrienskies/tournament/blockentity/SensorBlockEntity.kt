package org.valkyrienskies.tournament.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.util.helper.Helper3d
import org.valkyrienskies.tournament.util.extension.clipIncludeShips
import org.valkyrienskies.tournament.util.math.lerp
import kotlin.math.min
import kotlin.math.max
import kotlin.math.abs
import org.valkyrienskies.tournament.registry.TournamentBlockEntities

class SensorBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(TournamentBlockEntities.SENSOR.get(), pos, state) {

    private var lastCalculatedValue = 0
    private var lastCalculatedTime = 0L

    fun getResult(level: ServerLevel): Int {
        val facing = blockState.getValue(BlockStateProperties.FACING)

        
        val blockCenter = Vec3.atCenterOf(blockPos)

        
        val directionVector = when (facing) {
            Direction.NORTH -> Vec3(0.0, 0.0, -TournamentConfig.SERVER.sensorDistance)
            Direction.SOUTH -> Vec3(0.0, 0.0, TournamentConfig.SERVER.sensorDistance)
            Direction.WEST -> Vec3(-TournamentConfig.SERVER.sensorDistance, 0.0, 0.0)
            Direction.EAST -> Vec3(TournamentConfig.SERVER.sensorDistance, 0.0, 0.0)
            Direction.UP -> Vec3(0.0, TournamentConfig.SERVER.sensorDistance, 0.0)
            Direction.DOWN -> Vec3(0.0, -TournamentConfig.SERVER.sensorDistance, 0.0)
            else -> Vec3(0.0, 0.0, -TournamentConfig.SERVER.sensorDistance) 
        }

        
        
        val offsetStart = blockCenter.add(
            when (facing) {
                Direction.NORTH -> Vec3(0.0, 0.0, -0.45) 
                Direction.SOUTH -> Vec3(0.0, 0.0, 0.45)  
                Direction.WEST -> Vec3(-0.45, 0.0, 0.0)  
                Direction.EAST -> Vec3(0.45, 0.0, 0.0)   
                Direction.UP -> Vec3(0.0, 0.45, 0.0)     
                Direction.DOWN -> Vec3(0.0, -0.45, 0.0)  
                else -> Vec3(0.0, 0.0, -0.45)           
            }
        )
        
        
        val start = Helper3d.convertShipToWorldSpace(level, offsetStart)
        val end = Helper3d.convertShipToWorldSpace(level, blockCenter.add(directionVector))

        
        val clipResult = level.clipIncludeShips(
            ClipContext(
                start.toMinecraft(),
                end.toMinecraft(),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null 
            ),
            true 
        )

        return if (clipResult.type != HitResult.Type.MISS) {
            
            val hitPos = clipResult.location
            val hitPosAsVector3d = hitPos.toJOML()
            val distance = start.distance(hitPosAsVector3d) 
            val maxDistance = TournamentConfig.SERVER.sensorDistance

            
            if (maxDistance <= 0) {
                return 0 
            }

            
            val clampedDistance = min(distance, maxDistance)

            
            
            val normalizedDistance = clampedDistance / maxDistance
            val signalStrength = (15.0 - (normalizedDistance * 14.0)).toInt()

            
            max(0, min(15, signalStrength))
        } else {
            
            0
        }
    }

    companion object {
        
        
        fun tick(level: Level, pos: BlockPos, state: BlockState, be: BlockEntity) {
            be as SensorBlockEntity
            if(level.isClientSide)
                return

            
            val currentTime = System.currentTimeMillis()
            if (currentTime - be.lastCalculatedTime < 100) { 
                return
            }
            
            be.lastCalculatedTime = currentTime

            val newResult = be.getResult(level as ServerLevel)
            
            
            if (newResult != be.lastCalculatedValue) {
                be.lastCalculatedValue = newResult
                level.setBlock(pos, state.setValue(BlockStateProperties.POWER, newResult), 2)
            }
        }
    }
}