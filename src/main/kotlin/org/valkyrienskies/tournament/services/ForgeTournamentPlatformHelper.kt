package org.valkyrienskies.tournament.services

import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraft.client.Minecraft


class ForgeTournamentPlatformHelper : TournamentPlatformHelper {
    override fun loadBakedModel(modelLocation: ModelResourceLocation): BakedModel? {
        return if (FMLEnvironment.dist.isClient()) {
            
            try {
                val mc = Minecraft.getInstance()
                val modelManager = mc.modelManager
                modelManager.getModel(modelLocation)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }
}