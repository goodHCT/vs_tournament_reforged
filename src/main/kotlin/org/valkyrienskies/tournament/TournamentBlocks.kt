package org.valkyrienskies.tournament

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import org.valkyrienskies.tournament.blocks.*
import org.valkyrienskies.tournament.blocks.explosive.InstantExplosiveSmallBlock
import org.valkyrienskies.tournament.blocks.explosive.InstantExplosiveMediumBlock
import org.valkyrienskies.tournament.blocks.explosive.InstantExplosiveLargeBlock

object TournamentBlocks {
    val BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TournamentMod.MOD_ID)

    val SEAT = BLOCKS.register("seat") { SeatBlock() }
    val PROP_SMALL = BLOCKS.register("prop_small") { PropellerBlock(0.5, "small") }
    val PROP_BIG = BLOCKS.register("prop_big") { PropellerBlock(2.0, "big") }
    val EXPLOSIVE_INSTANT_SMALL = BLOCKS.register("explosive_instant_small") { InstantExplosiveSmallBlock() }
    val EXPLOSIVE_INSTANT_MEDIUM = BLOCKS.register("explosive_instant_medium") { InstantExplosiveMediumBlock() }
    val EXPLOSIVE_INSTANT_LARGE = BLOCKS.register("explosive_instant_large") { InstantExplosiveLargeBlock() }
    val SENSOR = BLOCKS.register("sensor") { SensorBlock() }
    val THRUSTER = BLOCKS.register("thruster") { ThrusterBlock({ 1.0 }, ParticleTypes.FLAME, { 5 }) }
    
    
    val BALLAST = BLOCKS.register("ballast") { BallastBlock() }
    
    val BALLOON = BLOCKS.register("balloon") { BalloonBlock() }  
    val POWERED_BALLOON = BLOCKS.register("powered_balloon") { PoweredBalloonBlock() }  
    val SPINNER = BLOCKS.register("spinner") { SpinnerBlock() }
    val FLOATER = BLOCKS.register("floater") { FloaterBlock() }
    val TINY_THRUSTER = BLOCKS.register("tiny_thruster") { TinyThrusterBlock() }
    val CUSTOM_THRUSTER = BLOCKS.register("custom_thruster") { CustomThrusterBlock() }
    val CUSTOM_SPINNER = BLOCKS.register("custom_spinner") { CustomSpinnerBlock() }
    val SIX_WAY_THRUSTER = BLOCKS.register("six_way_thruster") { SixWayThrusterBlock() }
    val SIX_WAY_SPINNER = BLOCKS.register("six_way_spinner") { SixWaySpinnerBlock() }
    
    val OBSIDIAN_SEAT = BLOCKS.register("obsidian_seat") { ObsidianSeatBlock() }
    
    
    val FLAP = BLOCKS.register("flap") { FlapBlock(BlockBehaviour.Properties.of().strength(1.0f, 2.0f)) }

    fun register(modEventBus: IEventBus) {
        
        BLOCKS.register(modEventBus)
    }
}