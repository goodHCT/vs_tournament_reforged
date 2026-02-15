package org.valkyrienskies.tournament.registry

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraft.world.level.block.entity.BlockEntityType
import org.valkyrienskies.tournament.TournamentMod
import org.valkyrienskies.tournament.blockentity.*
import org.valkyrienskies.tournament.blockentity.explosive.ExplosiveBlockEntity
import org.valkyrienskies.tournament.blockentity.BigPropellerBlockEntity
import org.valkyrienskies.tournament.blockentity.SmallPropellerBlockEntity
import org.valkyrienskies.tournament.blockentity.CustomSpinnerBlockEntity
import org.valkyrienskies.tournament.blockentity.FlapBlockEntity
import org.valkyrienskies.tournament.blocks.*
import org.valkyrienskies.tournament.TournamentBlocks
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import org.valkyrienskies.tournament.blockentity.render.PropellerBlockEntityRender
import org.valkyrienskies.tournament.TournamentModels

object TournamentBlockEntities {
    val BLOCKENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TournamentMod.MOD_ID)

    val SEAT = BLOCKENTITIES.register("seat") { 
        BlockEntityType.Builder.of(
            ::SeatEntity,
            TournamentBlocks.SEAT.get()
        ).build(null)
    }
    
    val SENSOR = BLOCKENTITIES.register("sensor") {
        BlockEntityType.Builder.of(
            ::SensorBlockEntity,
            TournamentBlocks.SENSOR.get()
        ).build(null)
    }
    
    val PROPELLER_SMALL = BLOCKENTITIES.register("propeller_small") {
        BlockEntityType.Builder.of(
            ::SmallPropellerBlockEntity,
            TournamentBlocks.PROP_SMALL.get()
        ).build(null)
    }
    
    val PROPELLER_BIG = BLOCKENTITIES.register("propeller_big") {
        BlockEntityType.Builder.of(
            ::BigPropellerBlockEntity,
            TournamentBlocks.PROP_BIG.get()
        ).build(null)
    }
    
    val EXPLOSIVE_INSTANT_SMALL = BLOCKENTITIES.register("explosive_instant_small") {
        BlockEntityType.Builder.of(
            ::ExplosiveBlockEntity,
            TournamentBlocks.EXPLOSIVE_INSTANT_SMALL.get()
        ).build(null)
    }
    
    val EXPLOSIVE_INSTANT_MEDIUM = BLOCKENTITIES.register("explosive_instant_medium") {
        BlockEntityType.Builder.of(
            ::ExplosiveBlockEntity,
            TournamentBlocks.EXPLOSIVE_INSTANT_MEDIUM.get()
        ).build(null)
    }
    
    val EXPLOSIVE_INSTANT_LARGE = BLOCKENTITIES.register("explosive_instant_large") {
        BlockEntityType.Builder.of(
            ::ExplosiveBlockEntity,
            TournamentBlocks.EXPLOSIVE_INSTANT_LARGE.get()
        ).build(null)
    }
    
    val CUSTOM_THRUSTER = BLOCKENTITIES.register("custom_thruster") {
        BlockEntityType.Builder.of(
            ::CustomThrusterBlockEntity,
            TournamentBlocks.CUSTOM_THRUSTER.get()
        ).build(null)
    }
    
    val CUSTOM_SPINNER = BLOCKENTITIES.register("custom_spinner") {
        BlockEntityType.Builder.of(
            ::CustomSpinnerBlockEntity,
            TournamentBlocks.CUSTOM_SPINNER.get()
        ).build(null)
    }
    
    val SIX_WAY_THRUSTER = BLOCKENTITIES.register("six_way_thruster") {
        BlockEntityType.Builder.of(
            ::SixWayThrusterBlockEntity,
            TournamentBlocks.SIX_WAY_THRUSTER.get()
        ).build(null)
    }
    
    val SIX_WAY_SPINNER = BLOCKENTITIES.register("six_way_spinner") {
        BlockEntityType.Builder.of(
            ::SixWaySpinnerBlockEntity,
            TournamentBlocks.SIX_WAY_SPINNER.get()
        ).build(null)
    }
    
    val FLAP = BLOCKENTITIES.register("flap") {
        BlockEntityType.Builder.of(
            ::FlapBlockEntity,
            TournamentBlocks.FLAP.get()
        ).build(null)
    }

    fun register(modEventBus: IEventBus) {
        
        BLOCKENTITIES.register(modEventBus)
    }

    @OnlyIn(Dist.CLIENT)
    fun initClientRenderers(clientRenderers: TournamentMod.ClientRenderers) {
        clientRenderers.registerBlockEntityRenderer(PROPELLER_SMALL.get()) { 
            PropellerBlockEntityRender<SmallPropellerBlockEntity>(TournamentModels.PROP_SMALL) 
        }
        clientRenderers.registerBlockEntityRenderer(PROPELLER_BIG.get()) { 
            PropellerBlockEntityRender<BigPropellerBlockEntity>(TournamentModels.PROP_BIG) 
        }
    }
}