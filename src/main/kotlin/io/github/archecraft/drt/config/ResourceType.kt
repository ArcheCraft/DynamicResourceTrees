package io.github.archecraft.drt.config

import net.minecraft.util.*
import java.awt.*
import java.util.*

class ResourceType(val name: String, val drop: Optional<Drop>, val color: Color) {
    override fun toString(): String {
        return "[$name : #${color.rgb.toString(16)}" + drop.map { ", $it" }.orElse("") + "]"
    }
    
    
    class Drop(val id: ResourceLocation, val amount: Int, val chance: Int) {
        override fun toString(): String {
            return "[${id} : ${amount}x ${chance}]"
        }
    }
}