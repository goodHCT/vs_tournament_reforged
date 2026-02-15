package org.valkyrienskies.tournament.forge.client

import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.valkyrienskies.tournament.TournamentBlocks

@Mod.EventBusSubscriber(value = [Dist.CLIENT], bus = Mod.EventBusSubscriber.Bus.MOD)
object ClientProxy {

    @JvmStatic
    @SubscribeEvent
    fun clientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork {
            setRenderLayers()
        }
    }

    private fun setRenderLayers() {
        
        ItemBlockRenderTypes.setRenderLayer(TournamentBlocks.PROP_SMALL.get(), RenderType.cutout())
        ItemBlockRenderTypes.setRenderLayer(TournamentBlocks.PROP_BIG.get(), RenderType.cutout())
    }
}