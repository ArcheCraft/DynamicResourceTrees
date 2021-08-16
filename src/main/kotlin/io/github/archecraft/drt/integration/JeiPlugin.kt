package io.github.archecraft.drt.integration

import io.github.archecraft.drt.*
import io.github.archecraft.drt.config.*
import mezz.jei.api.*
import mezz.jei.api.JeiPlugin
import mezz.jei.api.constants.*
import mezz.jei.api.runtime.*
import net.minecraft.item.*
import net.minecraft.util.*
import net.minecraftforge.registries.*

@JeiPlugin
class JeiPlugin : IModPlugin {
    override fun getPluginUid(): ResourceLocation {
        return DynamicResourceTrees.resourceLocation(DynamicResourceTrees.MOD_ID)
    }
    
    override fun onRuntimeAvailable(jeiRuntime: IJeiRuntime) {
        jeiRuntime.ingredientManager.addIngredientsAtRuntime(VanillaTypes.ITEM, ConfigHandler.BAKED_COMMON.resources.flatMap { type ->
            listOf("${type.name}_leaves_v", "${type.name}_resin", "${type.name}_amber").mapNotNull { ForgeRegistries.ITEMS.getValue(DynamicResourceTrees.resourceLocation(it)) }.map { ItemStack(it, 1) }
        })
    }
}