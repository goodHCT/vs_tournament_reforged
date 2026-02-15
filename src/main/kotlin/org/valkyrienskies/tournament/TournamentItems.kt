package org.valkyrienskies.tournament

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import org.valkyrienskies.tournament.items.PulseGunItem
import org.valkyrienskies.tournament.items.SuctionGunItem
import org.valkyrienskies.tournament.items.ShipDeleteWandItem
import org.valkyrienskies.tournament.items.BaishiEducationItem
import org.valkyrienskies.tournament.items.DeprecatedPropellerItem
import org.valkyrienskies.tournament.items.FlapItem
import org.valkyrienskies.tournament.items.CustomThrusterItem
import org.valkyrienskies.tournament.items.CustomSpinnerItem
import org.valkyrienskies.tournament.items.SixWayThrusterItem
import org.valkyrienskies.tournament.items.SixWaySpinnerItem
import org.valkyrienskies.tournament.registry.CreativeTabs

object TournamentItems {
    val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TournamentMod.MOD_ID)

    
    val SPINNER = ITEMS.register("spinner") { BlockItem(TournamentBlocks.SPINNER.get(), Item.Properties()) }
    val THRUSTER = ITEMS.register("thruster") { BlockItem(TournamentBlocks.THRUSTER.get(), Item.Properties()) }
    val TINY_THRUSTER = ITEMS.register("tiny_thruster") { BlockItem(TournamentBlocks.TINY_THRUSTER.get(), Item.Properties()) }
    val CUSTOM_THRUSTER = ITEMS.register("custom_thruster") { CustomThrusterItem(TournamentBlocks.CUSTOM_THRUSTER.get(), Item.Properties()) }
    val CUSTOM_SPINNER = ITEMS.register("custom_spinner") { CustomSpinnerItem(TournamentBlocks.CUSTOM_SPINNER.get(), Item.Properties()) }
    val SIX_WAY_THRUSTER = ITEMS.register("six_way_thruster") { SixWayThrusterItem(TournamentBlocks.SIX_WAY_THRUSTER.get(), Item.Properties()) }
    val SIX_WAY_SPINNER = ITEMS.register("six_way_spinner") { SixWaySpinnerItem(TournamentBlocks.SIX_WAY_SPINNER.get(), Item.Properties()) }
    
    
    val BALLAST = ITEMS.register("ballast") { BlockItem(TournamentBlocks.BALLAST.get(), Item.Properties()) }
    
    
    val SEAT = ITEMS.register("seat") { BlockItem(TournamentBlocks.SEAT.get(), Item.Properties()) }
    val FLOATER = ITEMS.register("floater") { BlockItem(TournamentBlocks.FLOATER.get(), Item.Properties()) }
    val OBSIDIAN_SEAT = ITEMS.register("obsidian_seat") { BlockItem(TournamentBlocks.OBSIDIAN_SEAT.get(), Item.Properties()) }
    
    
    val BALLOON = ITEMS.register("balloon") { BlockItem(TournamentBlocks.BALLOON.get(), Item.Properties()) }
    val POWERED_BALLOON = ITEMS.register("powered_balloon") { BlockItem(TournamentBlocks.POWERED_BALLOON.get(), Item.Properties()) }
    
    
    val PROP_SMALL = ITEMS.register("prop_small") { DeprecatedPropellerItem(TournamentBlocks.PROP_SMALL.get(), Item.Properties()) }
    val PROP_BIG = ITEMS.register("prop_big") { DeprecatedPropellerItem(TournamentBlocks.PROP_BIG.get(), Item.Properties()) }
    
    
    val FLAP = ITEMS.register("flap") { FlapItem(TournamentBlocks.FLAP.get(), Item.Properties()) }
    
    
    val EXPLOSIVE_INSTANT_SMALL = ITEMS.register("explosive_instant_small") { BlockItem(TournamentBlocks.EXPLOSIVE_INSTANT_SMALL.get(), Item.Properties()) }
    val EXPLOSIVE_INSTANT_MEDIUM = ITEMS.register("explosive_instant_medium") { BlockItem(TournamentBlocks.EXPLOSIVE_INSTANT_MEDIUM.get(), Item.Properties()) }
    val EXPLOSIVE_INSTANT_LARGE = ITEMS.register("explosive_instant_large") { BlockItem(TournamentBlocks.EXPLOSIVE_INSTANT_LARGE.get(), Item.Properties()) }
    
    
    val SENSOR = ITEMS.register("sensor") { BlockItem(TournamentBlocks.SENSOR.get(), Item.Properties()) }
    
    
    val PULSE_GUN = ITEMS.register("pulse_gun") { PulseGunItem() }
    val SUCTION_GUN = ITEMS.register("suction_gun") { SuctionGunItem() }
    val GRAB_GUN = ITEMS.register("grab_gun") { Item(Item.Properties().stacksTo(1)) }
    val DELETE_WAND = ITEMS.register("delete_wand") { ShipDeleteWandItem() }
    
    
    val DAMPENED_GUNPOWDER = ITEMS.register("dampened_gunpowder") { Item(Item.Properties()) }
    val UPGRADE_THRUSTER = ITEMS.register("upgrade_thruster") { Item(Item.Properties()) }
    
    
    val BAISHI_EDUCATION = ITEMS.register("baishi_education") { BaishiEducationItem(Item.Properties().stacksTo(64)) }

    fun register(bus: IEventBus) {
        ITEMS.register(bus)
    }
}