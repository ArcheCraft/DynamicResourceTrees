package io.github.archecraft.drt.config

import net.minecraft.util.*
import java.awt.*
import java.util.*

class ResourceType(val name: String, val drop: Optional<Drop>, val color: Color) {
    class Drop(val id: ResourceLocation, val amount: Int, val chance: Int)
}