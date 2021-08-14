package io.github.archecraft.drt.integration.kubejs

import dev.latvian.kubejs.item.*
import io.github.archecraft.drt.config.*
import io.github.archecraft.drt.config.ResourceType.*
import net.minecraft.util.*
import java.awt.*
import java.util.*

class ResourceTypeJS(private val name: String, private val color: Color) {
    private var drop = Optional.empty<Drop>()
    
    fun drop(item: ItemStackJS) = apply {
        drop = Optional.of(Drop(ResourceLocation(item.id), item.count, (1 / item.chance).toInt()))
    }
    
    fun toResourceType() = ResourceType(name, drop, color)
}