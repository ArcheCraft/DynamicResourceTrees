package io.github.archecraft.drt.trees

import com.ferreusveritas.dynamictrees.resources.*
import com.google.common.collect.*
import com.google.gson.*
import io.github.archecraft.drt.*
import io.github.archecraft.drt.config.*
import net.minecraft.resources.*
import net.minecraft.util.*
import org.apache.logging.log4j.*
import java.io.*
import java.util.function.*
import kotlin.io.path.*

class DRTTreesPack : TreeResourcePack(Path("dummy")) {
    private val resources by lazy {
        val table = HashBasedTable.create<String, String, JsonElement>()
        
        DynamicResourceTrees.LOGGER.log(Level.INFO, "Creating tree resource pack...")
        
        for (type in ConfigHandler.BAKED_COMMON.resources) {
            DynamicResourceTrees.LOGGER.log(Level.INFO, "Creating tree resources for type ${type.name}")
            
            val family = JsonObject()
            family.addProperty("common_leaves", "${DynamicResourceTrees.MOD_ID}:${type.name}")
            family.addProperty("common_species", "${DynamicResourceTrees.MOD_ID}:${type.name}")
            family.addProperty("primitive_log", "oak_log")
            family.addProperty("max_branch_radius", 8)
            family.addProperty("generate_stripped_branch", false)
            table.put(DynamicResourceTrees.MOD_ID, "families/${type.name}.json", family)
            
            val leaves = JsonObject()
            leaves.addProperty("primitive_leaves", "${DynamicResourceTrees.MOD_ID}:${type.name}_leaves_v")
            table.put(DynamicResourceTrees.MOD_ID, "leaves_properties/${type.name}.json", leaves)
            
            val species = JsonObject()
            species.addProperty("family", "${DynamicResourceTrees.MOD_ID}:${type.name}")
            species.addProperty("tapering", 0.4)
            species.addProperty("signal_energy", 16.0)
            species.addProperty("growth_rate", 0.8)
            species.addProperty("leaves_properties", "${DynamicResourceTrees.MOD_ID}:${type.name}")
            species.addProperty("primitive_sapling", "${DynamicResourceTrees.MOD_ID}:${type.name}_sapling_v")
            species.add("drop_creators", JsonArray().apply {
                add("log")
                add("seed")
                add("stick")
                add("${DynamicResourceTrees.MOD_ID}:resin_drops")
            })
            table.put(DynamicResourceTrees.MOD_ID, "species/${type.name}.json", species)
        }
        
        DynamicResourceTrees.LOGGER.log(Level.INFO, "Created tree resource pack!")
        DynamicResourceTrees.LOGGER.log(Level.DEBUG, table.toString())
        
        table
    }
    
    override fun getResource(type: ResourcePackType?, location: ResourceLocation): InputStream {
        val res = resources[location.namespace, location.path] ?: throw FileNotFoundException("Could not find tree resource for path '$location'.")
        return res.toString().byteInputStream()
    }
    
    override fun getResources(type: ResourcePackType?, namespace: String, pathIn: String, maxDepth: Int, filter: Predicate<String>): Collection<ResourceLocation> {
        return resources.row(namespace).filter { it.key.startsWith(pathIn) && filter.test(it.key) }.map { ResourceLocation(namespace, it.key) }
    }
    
    override fun getNamespaces(type: ResourcePackType?): Set<String> {
        return resources.rowKeySet()
    }
}