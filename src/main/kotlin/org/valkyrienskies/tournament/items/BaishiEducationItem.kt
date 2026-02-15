package org.valkyrienskies.tournament.items

import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import java.util.Random

class BaishiEducationItem(properties: Properties) : Item(properties) {

    companion object {
        private val JIAHAO_QUOTES = listOf(
            "Six-way thruster will apply force towards the activated face.",
            "Six-way spinner will apply torque according to the pattern on the activated face.",
            "The custom series, six-way series, and tournament flap all allow for customizable parameters. Try using In-Game NBTedit!"
            )

        private val RANDOM = Random()
    }

    override fun appendHoverText(
        stack: ItemStack,
        world: Level?,
        tooltip: MutableList<Component>,
        flag: TooltipFlag
    ) {
        tooltip.add(Component.literal("§fTutorial Item"))
        super.appendHoverText(stack, world, tooltip, flag)
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (!level.isClientSide) {
            
            val quote = JIAHAO_QUOTES[RANDOM.nextInt(JIAHAO_QUOTES.size)]
            player.sendSystemMessage(Component.literal("§f<Tutorial> §f$quote"))

            
            level.playSound(
                null,
                player.x, player.y, player.z,
                SoundEvents.HORSE_DEATH,
                SoundSource.PLAYERS,
                1.0F,
                1.0F
            )
        }

        return InteractionResultHolder.success(player.getItemInHand(hand))
    }
}