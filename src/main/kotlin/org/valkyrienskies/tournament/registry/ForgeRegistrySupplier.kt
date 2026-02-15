package org.valkyrienskies.tournament.registry

import net.minecraftforge.registries.RegistryObject


class ForgeRegistrySupplier<T>(private val registryObject: RegistryObject<T>) : RegistrySupplier<T> {
    override val name: String
        get() = registryObject.id.toString()

    override fun get(): T = registryObject.get()

    override fun toString(): String = registryObject.toString()
}