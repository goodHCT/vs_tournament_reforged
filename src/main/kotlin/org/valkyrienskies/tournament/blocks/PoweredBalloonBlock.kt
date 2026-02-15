package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.MapColor
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.ship.TournamentShips
import org.valkyrienskies.mod.common.getLoadedShipManagingPos


enum class BalloonState {
    INACTIVE,      
    ACTIVE,        
    DESTROYED      
}

class PoweredBalloonBlock : Block(
    Properties.of()
        .mapColor(MapColor.WOOL)
        .sound(SoundType.WOOL)
        .strength(1.0f, 2.0f)
) {

    init {
        registerDefaultState(defaultBlockState()
            .setValue(BlockStateProperties.POWER, 0)
            .setValue(BlockStateProperties.POWERED, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.POWER)
        builder.add(BlockStateProperties.POWERED)
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving)

        if (level as? ServerLevel == null) return

        val signal = level.getBestNeighborSignal(pos)

        if (signal != state.getValue(BlockStateProperties.POWER)) {
            
            updateBalloonEffectBySignal(level, pos, state, signal)
            
            level.setBlock(
                pos,
                state
                    .setValue(BlockStateProperties.POWER, signal)
                    .setValue(BlockStateProperties.POWERED, signal > 0),
                2
            )
        }
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        if (level.isClientSide) return
        level as ServerLevel

        val signal = level.getBestNeighborSignal(pos)

        
        updateBalloonEffectBySignal(level, pos, state, signal)

        if (signal == state.getValue(BlockStateProperties.POWER))
            return

        level.setBlock(
            pos,
            state
                .setValue(BlockStateProperties.POWER, signal)
                .setValue(BlockStateProperties.POWERED, signal > 0),
            2
        )
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (level.isClientSide) return
        level as ServerLevel

        
        transitionToState(level, pos, state, BalloonState.DESTROYED)

        super.onRemove(state, level, pos, newState, isMoving)
    }

    
    private fun updateBalloonEffectBySignal(level: ServerLevel, pos: BlockPos, state: BlockState, signal: Int) {
        val shipControl = getShipControl(level, pos)
        if (shipControl != null) {
            
            shipControl.removeBalloon(pos)
            
            
            if (signal > 0) {
                
                val power = signal.toDouble() * TournamentConfig.SERVER.poweredBalloonAnalogStrength
                shipControl.addBalloon(pos, power)
            }
            
        }
    }

    
    private fun transitionToStateBySignal(level: ServerLevel, pos: BlockPos, state: BlockState, newState: BalloonState, signal: Int) {
        val shipControl = getShipControl(level, pos)
        
        when (newState) {
            BalloonState.INACTIVE -> {
                
                shipControl?.removeBalloon(pos)
            }
            BalloonState.ACTIVE -> {
                
                val power = signal.toDouble() * TournamentConfig.SERVER.poweredBalloonAnalogStrength
                shipControl?.addBalloon(
                    pos,
                    power
                )
            }
            BalloonState.DESTROYED -> {
                
                shipControl?.removeBalloon(pos)
            }
        }
    }
    
    
    protected fun transitionToState(level: ServerLevel, pos: BlockPos, state: BlockState, newState: BalloonState) {
        val shipControl = getShipControl(level, pos)
        
        when (newState) {
            BalloonState.INACTIVE -> {
                
                shipControl?.removeBalloon(pos)
            }
            BalloonState.ACTIVE -> {
                
                if (shipControl != null) {
                    
                    shipControl.removeBalloon(pos)
                    
                    
                    val signal = level.getBestNeighborSignal(pos)
                    val power = signal.toDouble() * TournamentConfig.SERVER.poweredBalloonAnalogStrength
                    
                    shipControl.addBalloon(
                        pos,
                        power
                    )
                }
            }
            BalloonState.DESTROYED -> {
                
                shipControl?.removeBalloon(pos)
            }
        }
    }
    
    protected fun getShipControl(level: ServerLevel, pos: BlockPos) =
        (level.getLoadedShipManagingPos(pos)
            ?.let { TournamentShips.getOrCreate(it) }
                )
}