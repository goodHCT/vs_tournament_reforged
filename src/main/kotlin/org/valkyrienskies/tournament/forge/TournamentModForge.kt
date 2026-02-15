package org.valkyrienskies.tournament.forge

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.valkyrienskies.tournament.TournamentMod

@Mod(TournamentMod.MOD_ID)
class TournamentModForge {
    constructor() {
        val modEventBus = FMLJavaModLoadingContext.get().modEventBus
        
        
        TournamentMod.init(modEventBus)
    }
}