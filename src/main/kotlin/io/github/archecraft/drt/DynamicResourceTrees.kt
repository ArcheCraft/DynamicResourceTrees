package io.github.archecraft.drt

import com.ferreusveritas.dynamictrees.api.registry.*
import com.ferreusveritas.dynamictrees.resources.*
import dev.latvian.kubejs.script.ScriptType.*
import io.github.archecraft.drt.client.*
import io.github.archecraft.drt.config.*
import io.github.archecraft.drt.integration.kubejs.*
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
        
        ModBlocks.REGISTRY.register(MOD_BUS)
        MOD_BUS.addGenericListener(TreeRegistry::onDropCreatorRegistry)
        
        ResourceRegistryEventJS().post(STARTUP, "drt.resource.registry")
        
        runWhenOn(Dist.CLIENT) {
            Minecraft.getInstance().resourcePackRepository.addPackFinder(DRTResourcePackFinder(Minecraft.getInstance().resourceManager))
        }
        
        DTResourceRegistries.TREES_RESOURCE_MANAGER.addResourcePack(DRTTreesPack())
        
        ModBlocks.configLoaded()
    }
    
    fun resourceLocation(path: String) = ResourceLocation(MOD_ID, path)
}