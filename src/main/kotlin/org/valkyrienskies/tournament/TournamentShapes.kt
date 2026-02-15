package org.valkyrienskies.tournament

import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.tournament.util.RotShapes


object TournamentShapes {
    val WING: WingShape = WingShape()

    class WingShape {
        private val xAxisShape = Block.box(4.0, 0.0, 0.0, 12.0, 16.0, 16.0)
        private val yAxisShape = Block.box(0.0, 4.0, 0.0, 16.0, 12.0, 16.0)
        private val zAxisShape = Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 12.0)

        fun get(axis: Direction.Axis): VoxelShape = when (axis) {
            Direction.Axis.X -> xAxisShape
            Direction.Axis.Y -> yAxisShape
            Direction.Axis.Z -> zAxisShape
        }
    }
}