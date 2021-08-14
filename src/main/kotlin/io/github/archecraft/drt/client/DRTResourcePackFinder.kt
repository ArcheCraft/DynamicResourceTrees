package io.github.archecraft.drt.client

import io.github.archecraft.drt.*
import net.minecraft.resources.*
import net.minecraft.resources.PackCompatibility.*
import net.minecraft.resources.ResourcePackInfo.*
import net.minecraft.resources.ResourcePackInfo.Priority.*
import net.minecraft.resources.data.*
import net.minecraft.util.text.*
import java.util.function.*

class DRTResourcePackFinder(private val resources: IResourceManager) : IPackFinder {
    override fun loadPacks(register: Consumer<ResourcePackInfo>, p1: IFactory) {
        val pack = DRTResourcePack(resources)
        val metadataSection = PackMetadataSection(StringTextComponent("DynamicResourceTrees dynamic resources"), 6)
        register.accept(ResourcePackInfo("${DynamicResourceTrees.MOD_ID}:dynamic_resource_pack", true, { pack }, StringTextComponent(pack.name), metadataSection.description, COMPATIBLE, TOP, false, IPackNameDecorator.BUILT_IN, false))
    }
}