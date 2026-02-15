package org.valkyrienskies.tournament.event

import net.minecraft.world.item.CreativeModeTabs
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.valkyrienskies.tournament.TournamentMod
import org.valkyrienskies.tournament.TournamentItems

@Mod.EventBusSubscriber(modid = TournamentMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object CreativeModeTabEvent {

    @SubscribeEvent
    fun buildContents(event: BuildCreativeModeTabContentsEvent) {
        
        if (event.tabKey.location().path == "vs_tournament.main_tab") {
            
            
            try {
                
                event.accept(TournamentItems.PULSE_GUN.get())
                event.accept(TournamentItems.SUCTION_GUN.get())
                event.accept(TournamentItems.DELETE_WAND.get())
                event.accept(TournamentItems.UPGRADE_THRUSTER.get())
                
                
                event.accept(TournamentItems.DAMPENED_GUNPOWDER.get())
                
                
                event.accept(TournamentItems.THRUSTER.get())
                event.accept(TournamentItems.TINY_THRUSTER.get())
                event.accept(TournamentItems.CUSTOM_THRUSTER.get())
                event.accept(TournamentItems.SIX_WAY_THRUSTER.get())
                
                event.accept(TournamentItems.SPINNER.get())
                event.accept(TournamentItems.CUSTOM_SPINNER.get())
                event.accept(TournamentItems.SIX_WAY_SPINNER.get())
                
                event.accept(TournamentItems.SENSOR.get())
                
                
                event.accept(TournamentItems.SEAT.get())
                event.accept(TournamentItems.BALLAST.get())
                event.accept(TournamentItems.FLOATER.get())
                
                
                event.accept(TournamentItems.BALLOON.get())
                event.accept(TournamentItems.POWERED_BALLOON.get())
                
                
                event.accept(TournamentItems.PROP_SMALL.get())
                event.accept(TournamentItems.PROP_BIG.get())
                
                
                event.accept(TournamentItems.FLAP.get())
                
                
                event.accept(TournamentItems.EXPLOSIVE_INSTANT_SMALL.get())
                event.accept(TournamentItems.EXPLOSIVE_INSTANT_MEDIUM.get())
                event.accept(TournamentItems.EXPLOSIVE_INSTANT_LARGE.get())
            } catch (e: Exception) {
                
                println("Failed to add items to creative tab: \${e.message}")
            }
        } else if (event.tabKey == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            try {
                event.accept(TournamentItems.PULSE_GUN.get())
                event.accept(TournamentItems.SUCTION_GUN.get())
                event.accept(TournamentItems.DELETE_WAND.get())
                event.accept(TournamentItems.UPGRADE_THRUSTER.get())
            } catch (e: Exception) {
                println("Failed to add items to TOOLS_AND_UTILITIES tab: \${e.message}")
            }
        } else if (event.tabKey == CreativeModeTabs.INGREDIENTS) {
            try {
                event.accept(TournamentItems.DAMPENED_GUNPOWDER.get()) 
            } catch (e: Exception) {
                println("Failed to add items to INGREDIENTS tab: \${e.message}")
            }
        } else if (event.tabKey == CreativeModeTabs.BUILDING_BLOCKS) {
            try {
                event.accept(TournamentItems.SEAT.get())
                event.accept(TournamentItems.BALLAST.get())
                event.accept(TournamentItems.FLOATER.get())
                event.accept(TournamentItems.BALLOON.get())
                event.accept(TournamentItems.POWERED_BALLOON.get())
                event.accept(TournamentItems.PROP_SMALL.get())
                event.accept(TournamentItems.PROP_BIG.get())
                event.accept(TournamentItems.THRUSTER.get())
                event.accept(TournamentItems.TINY_THRUSTER.get())
                event.accept(TournamentItems.SENSOR.get())
                event.accept(TournamentItems.SPINNER.get())
                event.accept(TournamentItems.CUSTOM_THRUSTER.get())
                event.accept(TournamentItems.CUSTOM_SPINNER.get())
                event.accept(TournamentItems.SIX_WAY_THRUSTER.get())
                event.accept(TournamentItems.SIX_WAY_SPINNER.get())
                event.accept(TournamentItems.FLAP.get())
                event.accept(TournamentItems.EXPLOSIVE_INSTANT_SMALL.get())
                event.accept(TournamentItems.EXPLOSIVE_INSTANT_MEDIUM.get())
                event.accept(TournamentItems.EXPLOSIVE_INSTANT_LARGE.get())
            } catch (e: Exception) {
                println("Failed to add items to BUILDING_BLOCKS tab: \${e.message}")
            }
        } else if (event.tabKey == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            try {
                event.accept(TournamentItems.SENSOR.get())
                event.accept(TournamentItems.THRUSTER.get())
                event.accept(TournamentItems.TINY_THRUSTER.get())
                event.accept(TournamentItems.SPINNER.get())
                event.accept(TournamentItems.CUSTOM_THRUSTER.get())
                event.accept(TournamentItems.CUSTOM_SPINNER.get())
                event.accept(TournamentItems.SIX_WAY_THRUSTER.get())
                event.accept(TournamentItems.SIX_WAY_SPINNER.get())
                event.accept(TournamentItems.FLAP.get())
            } catch (e: Exception) {
                println("Failed to add items to FUNCTIONAL_BLOCKS tab: \${e.message}")
            }
        }
    }
}