package org.valkyrienskies.tournament.registry

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

interface DeferredRegister<T> : Iterable<RegistrySupplier<T>> {
    fun <I : T> register(name: String, builder: () -> I): RegistrySupplier<I>
    fun applyAll()
}

object DeferredRegisterFactory {
    fun <T> create(modId: String, registry: ResourceKey<Registry<T>>): DeferredRegister<T> {
        
        return ForgeDeferredRegister(modId, registry)
    }
}