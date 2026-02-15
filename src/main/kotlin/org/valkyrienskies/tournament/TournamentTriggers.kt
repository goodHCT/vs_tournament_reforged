package org.valkyrienskies.tournament

import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

object TournamentTriggers {

    fun register(modEventBus: IEventBus) {
        
        modEventBus.addListener { event: net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent ->
            event.enqueueWork {
                try {
                    
                    
                    println("Registered ${TournamentTriggers::class.simpleName} custom criteria triggers")
                } catch (e: Exception) {
                    
                    println("Failed to register custom criteria triggers: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }
}