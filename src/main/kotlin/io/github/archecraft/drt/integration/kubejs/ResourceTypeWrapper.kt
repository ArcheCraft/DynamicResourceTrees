package io.github.archecraft.drt.integration.kubejs

import dev.latvian.kubejs.item.*
import io.github.archecraft.drt.*

object ResourceTypeWrapper {
    fun seed(name: String): ItemStackJS {
        return ItemStackJS.of(DynamicResourceTrees.resourceLocation("${name}_seed"))
    }
    
    fun resin(name: String): ItemStackJS {
        return ItemStackJS.of(DynamicResourceTrees.resourceLocation("${name}_resin"))
    }
    
    fun amber(name: String): ItemStackJS {
        return ItemStackJS.of(DynamicResourceTrees.resourceLocation("${name}_amber"))
    }
}