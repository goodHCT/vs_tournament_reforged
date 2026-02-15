package org.valkyrienskies.tournament.blocks.explosive

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.MapColor
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.tournament.TournamentBlocks
import org.valkyrienskies.tournament.util.algo.Algo3d
import org.valkyrienskies.tournament.util.extension.toBlock

class InstantExplosiveMediumBlock : AbstractExplosiveBlock() {

    init {
        this.setupBlockProperties()
    }

    private fun setupBlockProperties() {
        
    }

    override fun explode(level: ServerLevel, pos: BlockPos) {
        
        level.explode(
            null,
            pos.x.toDouble() + 0.5,
            pos.y.toDouble() + 0.5,
            pos.z.toDouble() + 0.5,
            getExplosionStrength(), 
            Level.ExplosionInteraction.BLOCK
        )
        
        
        val range = (getExplosionStrength() / 2.0).toInt() + 1 
        for (x in -range..range) {
            for (y in -range..range) {
                for (z in -range..range) {
                    val checkPos = BlockPos(pos.x + x, pos.y + y, pos.z + z)
                    if (level.hasChunkAt(checkPos)) {
                        val blockState = level.getBlockState(checkPos)
                        val block = blockState.block
                        
                        
                        if (block == Blocks.TNT ||

                            block == TournamentBlocks.EXPLOSIVE_INSTANT_SMALL.get() ||
                            block == TournamentBlocks.EXPLOSIVE_INSTANT_MEDIUM.get() ||
                            block == TournamentBlocks.EXPLOSIVE_INSTANT_LARGE.get()) {
                            
                            
                            if (block is AbstractExplosiveBlock) {
                                
                                if (level.getBlockEntity(checkPos) == null) {
                                    
                                    block.explode(level, checkPos)
                                }
                            } else if (block == Blocks.TNT) {
                                
                                if (blockState.hasProperty(net.minecraft.world.level.block.TntBlock.UNSTABLE)) {
                                    level.setBlock(checkPos, Blocks.TNT.defaultBlockState().setValue(net.minecraft.world.level.block.TntBlock.UNSTABLE, true), 3)
                                } else {
                                    
                                    block.onCaughtFire(blockState, level, checkPos, null, null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun explosionTicks(): Int {
        return 0 
    }
    
    override fun getExplosionStrength(): Float = 5.0F 
}