package org.valkyrienskies.tournament.blocks

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.MapColor
import org.valkyrienskies.tournament.TournamentConfig

class TinyThrusterBlock : ThrusterBlock(
    { TournamentConfig.SERVER.thrusterTinyForceMultiplier }, 
    ParticleTypes.FLAME, 
    { TournamentConfig.SERVER.thrusterTiersTiny } 
) {
    init {
        
        this.setupBlockProperties()
    }

    private fun setupBlockProperties() {
        
        
    }
}