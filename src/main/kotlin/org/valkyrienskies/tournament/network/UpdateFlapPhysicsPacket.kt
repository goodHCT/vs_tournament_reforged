package org.valkyrienskies.tournament.network

import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.network.NetworkEvent
import org.valkyrienskies.tournament.blockentity.FlapBlockEntity
import java.util.function.Supplier

class UpdateFlapPhysicsPacket(
    val pos: BlockPos,
    val wingPower: Double,
    val wingDrag: Double,
    val wingBreakingForce: Double
) {
    companion object {
        @JvmStatic
        fun encode(packet: UpdateFlapPhysicsPacket, buf: FriendlyByteBuf) {
            buf.writeBlockPos(packet.pos)
            buf.writeDouble(packet.wingPower)
            buf.writeDouble(packet.wingDrag)
            buf.writeDouble(packet.wingBreakingForce)
        }

        @JvmStatic
        fun decode(buf: FriendlyByteBuf): UpdateFlapPhysicsPacket {
            return UpdateFlapPhysicsPacket(
                buf.readBlockPos(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble()
            )
        }

        @JvmStatic
        fun handle(packet: UpdateFlapPhysicsPacket, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                val sender = ctx.get().sender
                if (sender is ServerPlayer) {
                    val level = sender.level()
                    val blockEntity = level.getBlockEntity(packet.pos)
                    
                    if (blockEntity is FlapBlockEntity) {
                        
                        blockEntity.wingPower = packet.wingPower
                        blockEntity.wingDrag = packet.wingDrag
                        blockEntity.wingBreakingForce = packet.wingBreakingForce
                    }
                }
            }
            ctx.get().packetHandled = true
        }
    }
}