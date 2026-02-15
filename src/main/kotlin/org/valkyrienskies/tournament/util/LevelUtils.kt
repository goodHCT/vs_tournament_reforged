package org.valkyrienskies.tournament.util

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.storage.DimensionDataStorage
import org.valkyrienskies.tournament.TournamentMod

fun ServerLevel.getData(name: String, factory: (net.minecraft.nbt.CompoundTag) -> net.minecraft.world.level.saveddata.SavedData): net.minecraft.world.level.saveddata.SavedData {
    val storage: net.minecraft.world.level.storage.DimensionDataStorage = this.server.overworld().dataStorage
    val data = storage.computeIfAbsent(
        factory,
        { factory(net.minecraft.nbt.CompoundTag()) },
        name
    )
    
    return data
}