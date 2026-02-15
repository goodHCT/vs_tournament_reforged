package org.valkyrienskies.tournament.util.extension

import net.minecraft.world.level.Level
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toMinecraft

fun Level.clipIncludeShips(rayTraceContext: ClipContext, includeShips: Boolean = true): BlockHitResult {
    
    if (!includeShips) {
        return this.clip(rayTraceContext)
    }

    
    val start = rayTraceContext.from
    val end = rayTraceContext.to

    
    val startShip = this.getShipObjectManagingPos(start.toJOML())
    val endShip = this.getShipObjectManagingPos(end.toJOML())

    
    return if (startShip != null || endShip != null) {
        
        
        this.clip(rayTraceContext)
    } else {
        this.clip(rayTraceContext)
    }
}