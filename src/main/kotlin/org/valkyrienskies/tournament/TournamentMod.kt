package org.valkyrienskies.tournament

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import org.valkyrienskies.core.api.attachment.getAttachment
import org.valkyrienskies.core.impl.hooks.VSEvents
import org.valkyrienskies.mod.api.vsApi
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.tournament.ship.*
import org.valkyrienskies.tournament.registry.TournamentBlockEntities
import org.valkyrienskies.tournament.registry.TournamentCreativeTabs
import org.valkyrienskies.tournament.network.PacketHandler
import org.valkyrienskies.tournament.config.TournamentConfigWrapper

object TournamentMod {
    const val MOD_ID = "vs_tournament"

    @JvmStatic
    fun init(modEventBus: net.minecraftforge.eventbus.api.IEventBus) {
        
        TournamentConfigWrapper.registerConfigs()
        
        
        TournamentBlocks.register(modEventBus)
        TournamentItems.register(modEventBus)
        TournamentWeights.register()
        TournamentBlockEntities.register(modEventBus)  
        TournamentCreativeTabs.register(modEventBus)  
        
        
        TournamentTriggers.register(modEventBus)

        
        modEventBus.addListener { event: FMLCommonSetupEvent ->
            event.enqueueWork {
                
                TournamentConfigWrapper.syncConfigValues()
                vsApi.registerAttachment(TournamentShips::class.java)  
                
                
                TournamentWeights.register()
                
                
                PacketHandler.register()
            }
        }
    }

    @JvmStatic
    fun initClient() {

    }

    interface ClientRenderers {
        fun <T: BlockEntity> registerBlockEntityRenderer(t: BlockEntityType<T>, r: BlockEntityRendererProvider<T>)
    }

    @JvmStatic
    fun initClientRenderers(clientRenderers: TournamentMod.ClientRenderers) {
        TournamentBlockEntities.initClientRenderers(clientRenderers)
    }
}