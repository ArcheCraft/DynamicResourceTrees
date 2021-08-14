package io.github.archecraft.drt.integration

import dev.latvian.kubejs.KubeJSPlugin
import dev.latvian.kubejs.script.*
import io.github.archecraft.drt.integration.kubejs.*

class KubeJSPlugin : KubeJSPlugin() {
    override fun addBindings(event: BindingsEvent) {
        event.add("DRT", ResourceTypeWrapper)
    }
}