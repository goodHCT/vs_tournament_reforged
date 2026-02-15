package org.valkyrienskies.tournament.network

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel
import org.valkyrienskies.tournament.TournamentMod

object PacketHandler {
    private const val PROTOCOL_VERSION = "1.0.0"
    val CHANNEL: SimpleChannel = NetworkRegistry.newSimpleChannel(
        ResourceLocation(TournamentMod.MOD_ID, "main"),
        { PROTOCOL_VERSION },
        { true },
        { true }
    )

    fun register() {
        var id = 0
        CHANNEL.registerMessage(
            id++,
            UpdateFlapPhysicsPacket::class.java,
            UpdateFlapPhysicsPacket.Companion::encode,
            UpdateFlapPhysicsPacket.Companion::decode,
            UpdateFlapPhysicsPacket.Companion::handle
        )
    }
}