package io.github.archecraft.drt.integration

import dev.latvian.kubejs.script.ScriptType.*
import io.github.archecraft.drt.config.*
import io.github.archecraft.drt.integration.kubejs.*
import net.minecraftforge.fml.*

interface KubeJSProxy {
    val resourceTypes get() = emptyList<ResourceType>()
    
    fun fireResourceEvent() {
    }
    
    object With : KubeJSProxy {
        override val resourceTypes
            get() = ResourceRegistryEventJS.types.map(ResourceTypeJS::toResourceType)
        
        override fun fireResourceEvent() {
            ResourceRegistryEventJS().post(STARTUP, "drt.resource.registry")
        }
    }
    
    object Without : KubeJSProxy
    
    
    companion object {
        val PROXY by lazy {
            if (ModList.get().isLoaded("kubejs")) With else Without
        }
    }
}
