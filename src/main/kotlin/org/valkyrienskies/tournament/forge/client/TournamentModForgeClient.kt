package org.valkyrienskies.tournament.forge.client

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.valkyrienskies.tournament.TournamentMod
import org.valkyrienskies.tournament.TournamentBlocks

@Mod.EventBusSubscriber(value = [Dist.CLIENT], bus = Mod.EventBusSubscriber.Bus.MOD)
object TournamentModForgeClient {

    @SubscribeEvent
    fun onRegisterRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        
        TournamentMod.initClientRenderers(object : TournamentMod.ClientRenderers {
            override fun <T : BlockEntity> registerBlockEntityRenderer(
                t: BlockEntityType<T>,
                r: BlockEntityRendererProvider<T>
            ) {
                
                event.registerBlockEntityRenderer(t, r)
            }
        })
    }
}