package org.valkyrienskies.tournament.items

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block

class FlapItem(block: Block, properties: net.minecraft.world.item.Item.Properties) : BlockItem(block, properties) {

    override fun appendHoverText(
        stack: ItemStack,
        world: Level?,
        tooltip: MutableList<Component>,
        flag: TooltipFlag
    ) {
        tooltip.add(Component.translatable("item.vs_tournament.nbt.tooltip").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD))
        super.appendHoverText(stack, world, tooltip, flag)
    }
}