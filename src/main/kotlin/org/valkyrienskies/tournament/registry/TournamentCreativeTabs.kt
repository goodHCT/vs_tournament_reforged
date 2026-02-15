package org.valkyrienskies.tournament.registry

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.RegistryObject
import org.valkyrienskies.tournament.TournamentMod

object TournamentCreativeTabs {
    val CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TournamentMod.MOD_ID)

    val MAIN_TAB: RegistryObject<CreativeModeTab> = CREATIVE_MODE_TABS.register(
        "main_tab"
    ) {
        CreativeTabs.create()
    }

    fun register(modEventBus: IEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus)
    }
}