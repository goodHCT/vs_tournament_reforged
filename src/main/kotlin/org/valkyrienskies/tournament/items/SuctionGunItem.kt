package org.valkyrienskies.tournament.items

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.context.UseOnContext
import org.joml.Vector3d
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.ship.TournamentShips

class SuctionGunItem: Item(
    Properties().stacksTo(1)
){

    private var suctionForce: Vector3d? = null

    override fun getRarity(stack: ItemStack): Rarity {
        return Rarity.COMMON
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        // 使用负值来创建吸力（与脉冲喷枪相反）
        val force = -TournamentConfig.SERVER.pulseGunForce

        val player = context.player
        val blockPosition = context.clickedPos
        val blockLocation = context.clickLocation

        if(context.level.isClientSide || player == null) {
            return InteractionResult.PASS
        }

        val level = context.level
        if(level !is ServerLevel){
            return InteractionResult.PASS
        }

        val ship = level.getShipObjectManagingPos(blockPosition) ?: return InteractionResult.PASS

        suctionForce = player.lookAngle.normalize().scale(force * ship.inertiaData.mass).toJOML()

        TournamentShips.getOrCreate(ship).addPulse(blockLocation.toJOML(), suctionForce!!)

        return super.useOn(context)
    }
}