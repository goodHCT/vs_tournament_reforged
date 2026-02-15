package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.mod.common.getLoadedShipManagingPos
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.ship.TournamentShips

class BallastBlock : Block(
    Properties.of()
        .mapColor(MapColor.WOOD)
        .strength(1.0f, 2.0f)
) {
    companion object {
        private val SHAPE: VoxelShape = box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
        
        val POWER = BlockStateProperties.POWER
    }

    init {
        
        registerDefaultState(stateDefinition.any().setValue(POWER, 0))
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        neighborBlock: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        val newPower = level.getBestNeighborSignal(pos)
        val oldPower = state.getValue(POWER)
        
        if (newPower != oldPower) {
            level.setBlock(pos, state.setValue(POWER, newPower), 2)
        }
        
        super.neighborChanged(state, level, pos, neighborBlock, fromPos, isMoving)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(POWER)
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = SHAPE
    
    override fun isSignalSource(state: BlockState): Boolean = false
    
    
    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, movedByPiston: Boolean) {
        super.onPlace(state, level, pos, oldState, movedByPiston)
        
        if (level.isClientSide) return
        
        val ship = level.getLoadedShipManagingPos(pos)
        if (ship != null) {
            val shipData = TournamentShips.getOrCreate(ship)
            
            val power = state.getValue(POWER)
            
            val calculatedMass = calculateMass(power)
            if (power > 0) {
                shipData.addLightBallast(pos, calculatedMass)
            } else {
                shipData.addHeavyBallast(pos, calculatedMass)
            }
        }
    }
    
    
    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean) {
        if (state.`is`(newState.block)) return
        
        if (level.isClientSide) return
        
        val ship = level.getLoadedShipManagingPos(pos)
        if (ship != null) {
            val shipData = TournamentShips.getOrCreate(ship)
            shipData.removeBallast(pos)
        }
        
        super.onRemove(state, level, pos, newState, movedByPiston)
    }
    
    
    private fun calculateMass(power: Int): Double {
        val normalizedPower = power.coerceIn(0, 15) / 15.0 
        return TournamentConfig.SERVER.ballastWeight + 
               (TournamentConfig.SERVER.ballastNoWeight - TournamentConfig.SERVER.ballastWeight) * normalizedPower
    }
}