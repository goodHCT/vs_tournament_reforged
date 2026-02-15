package org.valkyrienskies.tournament.services

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import org.valkyrienskies.tournament.registry.DeferredRegister
import org.valkyrienskies.tournament.registry.ForgeDeferredRegister


class ForgeDeferredRegisterBackend : DeferredRegisterBackend {
    override fun <T> makeDeferredRegister(
        id: String,
        registry: ResourceKey<Registry<T>>
    ): DeferredRegister<T> = ForgeDeferredRegister(id, registry)
}