package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class FloaterBlock : Block(
    Properties.of()
        .mapColor(MapColor.WOOD)
        .sound(SoundType.WOOD)
        .strength(1.0f, 2.0f)
) {
    companion object {
        private val SHAPE: VoxelShape = box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    }

    override fun getCollisionShape(
        state: net.minecraft.world.level.block.state.BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = SHAPE
}