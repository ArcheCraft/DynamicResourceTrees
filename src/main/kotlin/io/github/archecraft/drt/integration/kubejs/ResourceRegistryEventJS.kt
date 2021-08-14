package io.github.archecraft.drt.integration.kubejs

import dev.latvian.kubejs.event.*
import java.awt.*

class ResourceRegistryEventJS : EventJS() {
    fun create(name: String, color: Any): ResourceTypeJS? {
        val type = if (color is String && color.startsWith("#")) {
            ResourceTypeJS(name, Color(color.substring(1).toInt(16)))
        } else if (color is Number) {
            ResourceTypeJS(name, Color(color.toInt()))
        } else null
        
        type?.let(types::add)
        
        return type
    }
    
    
    companion object {
        val types = mutableListOf<ResourceTypeJS>()
    }
}