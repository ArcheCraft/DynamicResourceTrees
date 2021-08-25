package io.github.archecraft.drt.config

import com.electronwill.nightconfig.core.file.*
import com.electronwill.nightconfig.core.io.WritingMode.*
import io.github.archecraft.drt.*
import io.github.archecraft.drt.config.ResourceType.*
import io.github.archecraft.drt.integration.*
import net.minecraft.util.*
import net.minecraftforge.common.*
import net.minecraftforge.common.ForgeConfigSpec.*
import net.minecraftforge.fml.loading.*
import org.apache.logging.log4j.*
import java.awt.*
import java.nio.file.*
import java.util.*

object ConfigHandler {
    private val COMMON: Common
    private val COMMON_SPEC: ForgeConfigSpec
    lateinit var BAKED_COMMON: BakedCommon
    
    class Common(builder: Builder) {
        val resources: ConfigValue<List<String>>
        
        init {
            val defaultResources = listOf(
                "dirt,unused,5e3e23",
                "diamond,unused,2edbc7",
                "cottonwood,minecraft:string#1#16,fff5ff"
            )
            
            resources = builder
                .comment(
                    """
                    The resources in this format:
                    name,drop,color
                    where
                        name : The name of the resource
                        drop : The extra drop for leaves in this format:
                            id#amount#metaData#dropChance
                            where
                                id : The namespaced id of the item
                                amount : The amount to drop
                                dropChance : The chance per broken leaves block to drop this item
                                    (chance will be 1:<number>)
                                    dropChance for acorns: 16
                            or 'unused' / 'null' for no extra drop
                        color : The hex value of the color for this resource
                    default: ["dirt,unused,573b30", "diamond,unused,45d6d4", "cottonwood,minecraft:string#1#16,fff5ff"]
                    """.trimIndent()
                )
                .defineListAllowEmpty(listOf("resources"), { defaultResources }) { it is String && parseType(it) != null }
        }
    }
    
    class BakedCommon(common: Common) {
        private val _resources: List<ResourceType>
        
        init {
            _resources = common.resources.get().mapNotNull(ConfigHandler::parseType)
        }
        
        val resources: List<ResourceType> get() = _resources + KubeJSProxy.PROXY.resourceTypes
        val resourceTypes: Map<String, ResourceType> get() = resources.associateBy { it.name }
    }
    
    fun parseType(input: String): ResourceType? {
        val parts = input.split(",")
        
        fun createResourceType(color: Color) = ResourceType(
            parts[0],
            if (parts[1].equals("unused", true) || parts[1].equals("null", true)) Optional.empty() else {
                val partsDrop = parts[1].split("#")
                
                if (partsDrop.size == 3) {
                    Optional.of(
                        Drop(
                            ResourceLocation(partsDrop[0]),
                            partsDrop[1].toInt(),
                            partsDrop[2].toInt()
                        )
                    )
                } else Optional.empty()
            },
            color
        )
        
        return when (parts.size) {
            3    -> createResourceType(Color(parts[2].toInt(16)))
            5    -> createResourceType(Color(parts[2].toInt(), parts[3].toInt(), parts[4].toInt()))
            else -> null
        }
    }
    
    init {
        val (config, spec) = Builder().configure(ConfigHandler::Common)
        COMMON_SPEC = spec
        COMMON = config
    }
    
    fun load() {
        val fileName = DynamicResourceTrees.MOD_ID + "-common.toml"
        
        DynamicResourceTrees.LOGGER.log(Level.INFO, "Loading config $fileName")
        
        val configPath = FMLPaths.CONFIGDIR.get().resolve(fileName)
        DynamicResourceTrees.LOGGER.log(Level.INFO, "Loading config at $configPath")
        val configData = CommentedFileConfig.builder(configPath).sync().preserveInsertionOrder().autosave().onFileNotFound { newfile, configFormat ->
            Files.createFile(newfile)
            configFormat.initEmptyFile(newfile)
            
            true
        }.writingMode(REPLACE).build()
        
        configData.load()
        
        COMMON_SPEC.setConfig(configData)
        
        BAKED_COMMON = BakedCommon(COMMON)
        
        DynamicResourceTrees.LOGGER.log(Level.INFO, "Loaded config at $configPath")
    }
}