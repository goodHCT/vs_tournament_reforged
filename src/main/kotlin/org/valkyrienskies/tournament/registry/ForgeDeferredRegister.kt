package org.valkyrienskies.tournament.registry

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.*

class ForgeDeferredRegister<T>(
    private val modId: String,
    private val registry: ResourceKey<Registry<T>>
) : DeferredRegister<T> {

    
    private val forgeRegister: net.minecraftforge.registries.DeferredRegister<T> = createForgeRegister()

    @Suppress("UNCHECKED_CAST")
    private fun createForgeRegister(): net.minecraftforge.registries.DeferredRegister<T> {
        return when (registry.location().path) {
            "blocks" -> net.minecraftforge.registries.DeferredRegister.create(ForgeRegistries.BLOCKS, modId) as net.minecraftforge.registries.DeferredRegister<T>
            "items" -> net.minecraftforge.registries.DeferredRegister.create(ForgeRegistries.ITEMS, modId) as net.minecraftforge.registries.DeferredRegister<T>
            "block_entity_types" -> net.minecraftforge.registries.DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, modId) as net.minecraftforge.registries.DeferredRegister<T>
            "menus" -> net.minecraftforge.registries.DeferredRegister.create(ForgeRegistries.MENU_TYPES, modId) as net.minecraftforge.registries.DeferredRegister<T>
            "entity_types" -> net.minecraftforge.registries.DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, modId) as net.minecraftforge.registries.DeferredRegister<T>
            else -> net.minecraftforge.registries.DeferredRegister.create(ForgeRegistries.BLOCKS, modId) as net.minecraftforge.registries.DeferredRegister<T>
        }
    }

    override fun <I : T> register(name: String, builder: () -> I): RegistrySupplier<I> {
        val registryObject = forgeRegister.register(name) { builder() }
        return ForgeRegistrySupplier(registryObject)
    }

    override fun applyAll() {
        
    }

    override fun iterator(): Iterator<RegistrySupplier<T>> {
        
        return emptyList<RegistrySupplier<T>>().iterator()
    }

    
    fun register(bus: net.minecraftforge.eventbus.api.IEventBus) {
        forgeRegister.register(bus)
    }
}