package org.valkyrienskies.tournament.blockentity.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.core.Direction
import net.minecraft.world.level.block.DirectionalBlock
import org.joml.AxisAngle4f
import org.joml.Quaternionf
import org.valkyrienskies.tournament.TournamentModels
import org.valkyrienskies.tournament.blockentity.PropellerBlockEntity

class PropellerBlockEntityRender<T: PropellerBlockEntity<T>>(
    val model: TournamentModels.Model
): BlockEntityRenderer<T> {

    override fun render(
        be: T,
        partial: Float,
        pose: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        
        val rotation = be.rotation + be.speed * partial
        
        
        pose.pushPose()
        pose.translate(0.5, 0.5, 0.5) 
        
        
        val facing = be.blockState.getValue(DirectionalBlock.FACING)
        when (facing) {
            Direction.NORTH -> {} 
            Direction.SOUTH -> pose.mulPose(Quaternionf(AxisAngle4f(Math.toRadians(180.0).toFloat(), 0f, 1f, 0f)))
            Direction.WEST -> pose.mulPose(Quaternionf(AxisAngle4f(Math.toRadians(90.0).toFloat(), 0f, 1f, 0f)))
            Direction.EAST -> pose.mulPose(Quaternionf(AxisAngle4f(Math.toRadians(-90.0).toFloat(), 0f, 1f, 0f)))
            Direction.UP -> pose.mulPose(Quaternionf(AxisAngle4f(Math.toRadians(-90.0).toFloat(), 1f, 0f, 0f)))
            Direction.DOWN -> pose.mulPose(Quaternionf(AxisAngle4f(Math.toRadians(90.0).toFloat(), 1f, 0f, 0f)))
        }
        
        
        val rotationRad = (rotation % 360.0) * Math.PI / 180.0
        when (facing) {
            Direction.NORTH, Direction.SOUTH -> pose.mulPose(Quaternionf(AxisAngle4f(rotationRad.toFloat(), 0f, 1f, 0f))) 
            Direction.WEST, Direction.EAST -> pose.mulPose(Quaternionf(AxisAngle4f(rotationRad.toFloat(), 0f, 1f, 0f))) 
            Direction.UP, Direction.DOWN -> pose.mulPose(Quaternionf(AxisAngle4f(rotationRad.toFloat(), 0f, 0f, 1f))) 
            else -> pose.mulPose(Quaternionf(AxisAngle4f(rotationRad.toFloat(), 0f, 1f, 0f)))
        }
        
        pose.translate(-0.5, -0.5, -0.5) 
        
        
        model.renderer.render(
            pose,
            be,
            bufferSource,
            packedLight,
            packedOverlay
        )
        
        pose.popPose() 
    }
}