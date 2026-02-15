package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.joml.Math
import org.valkyrienskies.mod.common.BlockStateInfo
import org.valkyrienskies.mod.common.BlockStateInfoProvider
import org.valkyrienskies.tournament.blocks.BallastBlock

object TournamentWeights {

    fun register() {
        Registry.register(BlockStateInfo.REGISTRY, ResourceLocation(TournamentMod.MOD_ID, "ballast"), Ballast)
    }
    
    
    object Ballast: BlockStateInfoProvider {
        override val priority: Int
            get() = 200

        override fun getBlockStateMass(blockState: BlockState): Double? =
            if (blockState.block is BallastBlock)
                Math.lerp(
                    TournamentConfig.SERVER.ballastWeight,
                    TournamentConfig.SERVER.ballastNoWeight,
                    (kotlin.runCatching { blockState.getValue(BlockStateProperties.POWER) }.getOrNull() ?: 0) / 15.0
                )
            else
                null

        
        @Suppress("RETURN_TYPE_MISMATCH_ON_OVERRIDE")
        override fun getBlockStateType(blockState: BlockState): kotlin.Any? =
            null
    }
}