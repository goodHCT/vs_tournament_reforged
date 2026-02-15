package org.valkyrienskies.tournament.registry

import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.tournament.TournamentItems

object CreativeTabs {
    fun create(): CreativeModeTab {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.vs_tournament.main_tab"))
            .icon { 
                try {
                    ItemStack(TournamentItems.THRUSTER.get())
                } catch (e: Exception) {
                    
                    ItemStack(net.minecraft.world.item.Items.BARRIER)
                }
            }
            .displayItems { _, output ->
                try {
                    
                    listOf(
                        
                        TournamentItems.PULSE_GUN.get(),
                        TournamentItems.SUCTION_GUN.get(),
                        TournamentItems.DELETE_WAND.get(),
                        TournamentItems.BAISHI_EDUCATION.get(),
                        TournamentItems.UPGRADE_THRUSTER.get(),
                        
                        
                        TournamentItems.DAMPENED_GUNPOWDER.get(),
                        
                        
                        TournamentItems.THRUSTER.get(),
                        TournamentItems.TINY_THRUSTER.get(),
                        TournamentItems.CUSTOM_THRUSTER.get(),
                        TournamentItems.SIX_WAY_THRUSTER.get(),
                        
                        TournamentItems.SPINNER.get(),
                        TournamentItems.CUSTOM_SPINNER.get(),
                        TournamentItems.SIX_WAY_SPINNER.get(),
                        
                        TournamentItems.SENSOR.get(),
                        
                        
                        TournamentItems.SEAT.get(),
                        TournamentItems.OBSIDIAN_SEAT.get(),
                        TournamentItems.BALLAST.get(),
                        TournamentItems.FLOATER.get(),
                        
                        
                        TournamentItems.BALLOON.get(),
                        TournamentItems.POWERED_BALLOON.get(),
                        
                        
                        TournamentItems.PROP_SMALL.get(),
                        TournamentItems.PROP_BIG.get(),
                        
                        
                        TournamentItems.EXPLOSIVE_INSTANT_SMALL.get(),
                        TournamentItems.EXPLOSIVE_INSTANT_MEDIUM.get(),
                        TournamentItems.EXPLOSIVE_INSTANT_LARGE.get(),
                        
                        
                        TournamentItems.FLAP.get()
                    ).forEach { item ->
                         try {
                             output.accept(item)
                         } catch (e: Exception) {
                             
                         }
                     }
                    
                    
                } catch (e: Exception) {
                    
                    println("Failed to populate creative tab: ${e.message}")
                }
            }
            .build()
    }
}