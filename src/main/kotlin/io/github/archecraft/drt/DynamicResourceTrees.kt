package io.github.archecraft.drt

import com.ferreusveritas.dynamictrees.api.registry.*
import io.github.archecraft.drt.client.*
import io.github.archecraft.drt.config.*
import io.github.archecraft.drt.integration.*
import io.github.archecraft.drt.registry.*
import io.github.archecraft.drt.trees.*
import net.minecraft.client.*
import net.minecraft.util.*
import net.minecraftforge.api.distmarker.*
import net.minecraftforge.fml.common.*
import org.apache.logging.log4j.*
import thedarkcolour.kotlinforforge.forge.*

@Mod(DynamicResourceTrees.MOD_ID)
object DynamicResourceTrees {
    const val MOD_ID: String = "dynamicresourcetrees"
    
    val LOGGER: Logger = LogManager.getLogger()
    
    init {
        LOGGER.log(Level.INFO, "DynamicResourceTrees is initializing...")
        
        ConfigHandler.load()
        
        val registryHandler = RegistryHandler(MOD_ID)
        RegistryHandler.REGISTRY.register(registryHandler)
        MOD_BUS.register(registryHandler)
        
        MOD_BUS.addGenericListener(TreeRegistry::onDropCreatorRegistry)
        MOD_BUS.addListener(TreeRegistry::onTreeManager)
        
        KubeJSProxy.PROXY.fireResourceEvent()
        
        runWhenOn(Dist.CLIENT) {
            Minecraft.getInstance().resourcePackRepository.addPackFinder(DRTResourcePackFinder(Minecraft.getInstance().resourceManager))
        }
        
        ModBlocks.configLoaded()
        
        LOGGER.log(Level.DEBUG, "Resources: ${ConfigHandler.BAKED_COMMON.resources.joinToString()}")
        LOGGER.log(Level.DEBUG, "ResourceTypes: ${ConfigHandler.BAKED_COMMON.resourceTypes.entries.joinToString { "${it.key} -> ${it.value}" }}")
    }
    
    fun resourceLocation(path: String) = ResourceLocation(MOD_ID, path)
}