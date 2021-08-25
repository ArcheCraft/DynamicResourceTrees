package io.github.archecraft.drt.client

import com.ferreusveritas.dynamictrees.DynamicTrees
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.github.archecraft.drt.DynamicResourceTrees
import io.github.archecraft.drt.config.ConfigHandler
import net.minecraft.resources.IResourceManager
import net.minecraft.resources.IResourcePack
import net.minecraft.resources.ResourcePackFileNotFoundException
import net.minecraft.resources.ResourcePackType
import net.minecraft.resources.data.IMetadataSectionSerializer
import net.minecraft.util.ResourceLocation
import org.apache.logging.log4j.Level
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Path
import java.util.function.Predicate
import javax.imageio.ImageIO
import kotlin.io.path.createTempFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.outputStream

class DRTResourcePack(private val resources: IResourceManager) : IResourcePack {
    private val packType = ResourcePackType.CLIENT_RESOURCES
    private var cachedResources: MutableMap<ResourceLocation, JsonElement>? = null
    private var cachedTextures: MutableMap<ResourceLocation, Path>? = null
    
    override fun getRootResource(fileName: String): InputStream {
        throw ResourcePackFileNotFoundException(File("dynamic_assets"), fileName)
    }
    
    override fun getResource(type: ResourcePackType, location: ResourceLocation): InputStream {
        if (location.path.endsWith(".json")) {
            val p = location.path.substring(0, location.path.length - 5)
            val json = generateJsonFile(location.namespace, p)
            
            if (json != null) {
                return json.toString().byteInputStream()
            }
        }
        
        if (location.path.endsWith(".png")) {
            val p = location.path.substring(0, location.path.length - 4)
            val tex = generateTexture(location.namespace, p)
            
            if (tex != null) {
                return FileInputStream(tex.toFile())
            }
        }
        
        throw ResourcePackFileNotFoundException(File("dynamic_assets"), location.toString())
    }
    
    override fun hasResource(type: ResourcePackType, location: ResourceLocation): Boolean {
        if (location.path.endsWith(".json")) {
            val p = location.path.substring(0, location.path.length - 5)
            val json = generateJsonFile(location.namespace, p)
            
            if (json != null) {
                return true
            }
        }
        
        if (location.path.endsWith(".png")) {
            val p = location.path.substring(0, location.path.length - 4)
            val tex = generateTexture(location.namespace, p)
            
            if (tex != null) {
                return true
            }
        }
        
        return false
    }
    
    private fun getCachedResources(): Map<ResourceLocation, JsonElement> {
        if (cachedResources == null) {
            cachedResources = mutableMapOf()
            generateJsonFiles(cachedResources!!)
        }
        
        return cachedResources!!
    }
    
    private fun generateJsonFiles(map: MutableMap<ResourceLocation, JsonElement>) {
        DynamicResourceTrees.LOGGER.log(Level.INFO, "Generating json resources...")
        
        fun path(path: String) = DynamicResourceTrees.MOD_ID + ":" + path
        
        val lang = JsonObject()
        
        for (type in ConfigHandler.BAKED_COMMON.resources) {
            DynamicResourceTrees.LOGGER.log(Level.INFO, "Generating json resources for type ${type.name}")
            
            var json = JsonObject()
            var variants = JsonObject()
            var model = JsonObject()
            model.addProperty("model", path("block/${type.name}_branch"))
            variants.add("", model)
            json.add("variants", variants)
            map[DynamicResourceTrees.resourceLocation("blockstates/${type.name}_branch")] = json
            
            json = JsonObject()
            json.addProperty("loader", "dynamictrees:branch")
            var textures = JsonObject()
            textures.addProperty("bark", path("block/${type.name}_log"))
            textures.addProperty("rings", path("block/${type.name}_log_top"))
            json.add("textures", textures)
            map[DynamicResourceTrees.resourceLocation("models/block/${type.name}_branch")] = json
            
            json = JsonObject()
            variants = JsonObject()
            model = JsonObject()
            model.addProperty("model", path("block/${type.name}_leaves_v"))
            variants.add("", model)
            json.add("variants", variants)
            map[DynamicResourceTrees.resourceLocation("blockstates/${type.name}_leaves_v")] = json
            
            json = JsonObject()
            textures = JsonObject()
            textures.addProperty("all", path("block/${type.name}_leaves"))
            json.add("textures", textures)
            var elements = JsonObject()
            elements.add("from", JsonArray().apply {
                add(0)
                add(0)
                add(0)
            })
            elements.add("to", JsonArray().apply {
                add(16)
                add(16)
                add(16)
            })
            val faces = JsonObject()
            fun face(faces: JsonObject, name: String) {
                val face = JsonObject()
                face.add("uv", JsonArray().apply {
                    add(0)
                    add(0)
                    add(16)
                    add(16)
                })
                face.addProperty("texture", "#all")
                face.addProperty("cullface", name)
                faces.add(name, face)
            }
            face(faces, "down")
            face(faces, "up")
            face(faces, "north")
            face(faces, "south")
            face(faces, "west")
            face(faces, "east")
            elements.add("faces", faces)
            json.add("elements", JsonArray().apply {
                add(elements)
            })
            map[DynamicResourceTrees.resourceLocation("models/block/${type.name}_leaves_v")] = json
            
            json = JsonObject()
            json.addProperty("parent", path("block/${type.name}_leaves_v"))
            map[DynamicResourceTrees.resourceLocation("models/item/${type.name}_leaves_v")] = json
            
            json = JsonObject()
            variants = JsonObject()
            model = JsonObject()
            model.addProperty("model", path("block/${type.name}_leaves"))
            variants.add("", model)
            json.add("variants", variants)
            map[DynamicResourceTrees.resourceLocation("blockstates/${type.name}_leaves")] = json
            
            json = JsonObject()
            textures = JsonObject()
            textures.addProperty("all", path("block/${type.name}_leaves"))
            json.add("textures", textures)
            elements = JsonObject()
            elements.add("from", JsonArray().apply {
                add(0)
                add(0)
                add(0)
            })
            elements.add("to", JsonArray().apply {
                add(16)
                add(16)
                add(16)
            })
            elements.add("faces", faces)
            json.add("elements", JsonArray().apply {
                add(elements)
            })
            map[DynamicResourceTrees.resourceLocation("models/block/${type.name}_leaves")] = json
            
            json = JsonObject()
            variants = JsonObject()
            model = JsonObject()
            model.addProperty("model", path("block/${type.name}_sapling"))
            variants.add("", model)
            json.add("variants", variants)
            map[DynamicResourceTrees.resourceLocation("blockstates/${type.name}_sapling")] = json
            
            json = JsonObject()
            json.addProperty("parent", "dynamictrees:block/smartmodel/sapling")
            textures = JsonObject()
            textures.addProperty("particle", path("block/${type.name}_leaves"))
            textures.addProperty("log", path("block/${type.name}_log"))
            textures.addProperty("leaves", path("block/${type.name}_leaves"))
            json.add("textures", textures)
            map[DynamicResourceTrees.resourceLocation("models/block/${type.name}_sapling")] = json
            
            json = JsonObject()
            json.addProperty("parent", "dynamictrees:item/branch")
            textures = JsonObject()
            textures.addProperty("bark", path("block/${type.name}_log"))
            textures.addProperty("rings", path("block/${type.name}_log_top"))
            json.add("textures", textures)
            map[DynamicResourceTrees.resourceLocation("models/item/${type.name}_branch")] = json
            
            json = JsonObject()
            json.addProperty("parent", "dynamictrees:item/standard_seed")
            textures = JsonObject()
            textures.addProperty("layer0", path("item/${type.name}_seed"))
            json.add("textures", textures)
            map[DynamicResourceTrees.resourceLocation("models/item/${type.name}_seed")] = json
            
            json = JsonObject()
            variants = JsonObject()
            model = JsonObject()
            model.addProperty("model", path("block/${type.name}_log_horizontal"))
            model.addProperty("x", 90)
            model.addProperty("y", 90)
            variants.add("axis=x", model)
            model = JsonObject()
            model.addProperty("model", path("block/${type.name}_log"))
            variants.add("axis=y", model)
            model = JsonObject()
            model.addProperty("model", path("block/${type.name}_log_horizontal"))
            model.addProperty("x", 90)
            variants.add("axis=z", model)
            json.add("variants", variants)
            map[DynamicResourceTrees.resourceLocation("blockstates/${type.name}_log")] = json
            
            json = JsonObject()
            json.addProperty("parent", "minecraft:block/cube_column")
            textures = JsonObject()
            textures.addProperty("side", path("block/${type.name}_log"))
            textures.addProperty("end", path("block/${type.name}_log_top"))
            json.add("textures", textures)
            map[DynamicResourceTrees.resourceLocation("models/block/${type.name}_log")] = json
            
            json = JsonObject()
            json.addProperty("parent", "minecraft:block/cube_column_horizontal")
            textures = JsonObject()
            textures.addProperty("side", path("block/${type.name}_log"))
            textures.addProperty("end", path("block/${type.name}_log_top"))
            json.add("textures", textures)
            map[DynamicResourceTrees.resourceLocation("models/block/${type.name}_log_horizontal")] = json
            
            json = JsonObject()
            variants = JsonObject()
            model = JsonObject()
            model.addProperty("model", path("block/${type.name}_sapling_v"))
            variants.add("", model)
            json.add("variants", variants)
            map[DynamicResourceTrees.resourceLocation("blockstates/${type.name}_sapling_v")] = json
            
            json = JsonObject()
            json.addProperty("parent", "minecraft:block/cross")
            textures = JsonObject()
            textures.addProperty("cross", path("block/${type.name}_sapling"))
            json.add("textures", textures)
            map[DynamicResourceTrees.resourceLocation("models/block/${type.name}_sapling_v")] = json
            
            json = JsonObject()
            json.addProperty("parent", "item/generated")
            textures = JsonObject()
            textures.addProperty("layer0", path("block/${type.name}_sapling"))
            json.add("textures", textures)
            map[DynamicResourceTrees.resourceLocation("models/item/${type.name}_sapling_v")] = json
            
            json = JsonObject()
            json.addProperty("parent", "item/generated")
            textures = JsonObject()
            textures.addProperty("layer0", path("item/${type.name}_resin"))
            json.add("textures", textures)
            map[DynamicResourceTrees.resourceLocation("models/item/${type.name}_resin")] = json
            
            json = JsonObject()
            variants = JsonObject()
            model = JsonObject()
            model.addProperty("model", path("block/${type.name}_amber"))
            variants.add("", model)
            json.add("variants", variants)
            map[DynamicResourceTrees.resourceLocation("blockstates/${type.name}_amber")] = json
            
            json = JsonObject()
            json.addProperty("parent", "block/cube_all")
            textures = JsonObject()
            textures.addProperty("all", path("block/${type.name}_amber"))
            json.add("textures", textures)
            elements = JsonObject()
            elements.add("from", JsonArray().apply {
                add(0)
                add(0)
                add(0)
            })
            elements.add("to", JsonArray().apply {
                add(16)
                add(16)
                add(16)
            })
            elements.add("faces", faces)
            json.add("elements", JsonArray().apply {
                add(elements)
            })
            map[DynamicResourceTrees.resourceLocation("models/block/${type.name}_amber")] = json
            
            json = JsonObject()
            json.addProperty("parent", path("block/${type.name}_amber"))
            map[DynamicResourceTrees.resourceLocation("models/item/${type.name}_amber")] = json
            
            
            lang.addProperty("item.${DynamicResourceTrees.MOD_ID}.${type.name}_seed", type.name.split("_").joinToString(separator = " ") { s -> s.replaceFirstChar { it.uppercase() } } + " Acorn")
            lang.addProperty("block.${DynamicResourceTrees.MOD_ID}.${type.name}_leaves_v", type.name.split("_").joinToString(separator = " ") { s -> s.replaceFirstChar { it.uppercase() } } + " Leaves")
            lang.addProperty("block.${DynamicResourceTrees.MOD_ID}.${type.name}_branch", type.name.split("_").joinToString(separator = " ") { s -> s.replaceFirstChar { it.uppercase() } } + " Tree")
            lang.addProperty("item.${DynamicResourceTrees.MOD_ID}.${type.name}_resin", type.name.split("_").joinToString(separator = " ") { s -> s.replaceFirstChar { it.uppercase() } } + " Resin")
            lang.addProperty("block.${DynamicResourceTrees.MOD_ID}.${type.name}_amber", type.name.split("_").joinToString(separator = " ") { s -> s.replaceFirstChar { it.uppercase() } } + " Amber")
        }
        
        map[DynamicResourceTrees.resourceLocation("lang/en_us")] = lang
        
        DynamicResourceTrees.LOGGER.log(Level.INFO, "Generated json resources")
    }
    
    private fun generateJsonFile(namespace: String, path: String): JsonElement? {
        return getCachedResources()[ResourceLocation(namespace, path)]
    }
    
    private fun getCachedTextures(): Map<ResourceLocation, Path> {
        if (cachedTextures == null) {
            cachedTextures = mutableMapOf()
            generateTextures(cachedTextures!!)
        }
        
        return cachedTextures!!
    }
    
    private fun generateTextures(map: MutableMap<ResourceLocation, Path>) {
        DynamicResourceTrees.LOGGER.log(Level.INFO, "Creating textures...")
        
        fun BufferedImage.modify(colorMod: Color): BufferedImage {
            val newImage = BufferedImage(width, height, TYPE_INT_ARGB)
            
            for (i in 0 until newImage.height) {
                for (j in 0 until newImage.width) {
                    val old = Color(getRGB(j, i), true)
                    val mod = (old.red + old.green + old.blue) / (255.0 * 3)
                    
                    if (old.alpha == 0) {
                        newImage.setRGB(j, i, old.rgb)
                    } else {
                        val new = Color((mod * colorMod.red).toInt(), (mod * colorMod.green).toInt(), (mod * colorMod.blue).toInt(), old.alpha)
                        newImage.setRGB(j, i, new.rgb)
                    }
                }
            }
            
            return newImage
        }
        
        fun BufferedImage.modify(mask: BufferedImage, colorMod: Color): BufferedImage {
            val newImage = BufferedImage(width, height, TYPE_INT_ARGB)
            
            for (i in 0 until newImage.height) {
                for (j in 0 until newImage.width) {
                    val modifier = Color(mask.getRGB(j, i), true)
                    val old = Color(getRGB(j, i), true)
                    val mod = (modifier.red + modifier.green + modifier.blue) / (255.0 * 3)
                    
                    if (modifier.alpha != 0 && old.alpha != 0) {
                        val new = Color((mod * colorMod.red).toInt(), (mod * colorMod.green).toInt(), (mod * colorMod.blue).toInt(), old.alpha)
                        newImage.setRGB(j, i, new.rgb)
                    } else {
                        newImage.setRGB(j, i, old.rgb)
                    }
                }
            }
            
            return newImage
        }
        
        fun BufferedImage.filter(predicate: (Color) -> Boolean): BufferedImage {
            val newImage = BufferedImage(width, height, TYPE_INT_ARGB)
            
            for (i in 0 until newImage.height) {
                for (j in 0 until newImage.width) {
                    val color = Color(getRGB(j, i), true)
                    
                    if (predicate(color)) {
                        newImage.setRGB(j, i, color.rgb)
                    } else {
                        newImage.setRGB(j, i, Color(0, 0, 0, 0).rgb)
                    }
                }
            }
            
            return newImage
        }
        
        fun BufferedImage.grayscale(): BufferedImage {
            val newImage = BufferedImage(width, height, TYPE_INT_ARGB)
            
            for (i in 0 until newImage.height) {
                for (j in 0 until newImage.width) {
                    val color = Color(getRGB(j, i), true)
                    val gs = (0.299 * color.red + 0.587 * color.green + 0.114 * color.blue).toInt()
                    val new = Color(gs, gs, gs, color.alpha)
                    newImage.setRGB(j, i, new.rgb)
                }
            }
            
            return newImage
        }
        
        fun BufferedImage.createTemp(): Path {
            val tempFile = createTempFile(suffix = ".png")
            tempFile.outputStream().buffered().use { ImageIO.write(this, "png", it) }
            DynamicResourceTrees.LOGGER.log(Level.DEBUG, "Creating temp file at ${tempFile.invariantSeparatorsPathString}")
            return tempFile
        }
        
        fun texture(path: String) = resources.getResource(DynamicResourceTrees.resourceLocation("textures/$path.png")).inputStream.use { ImageIO.read(it) }
        fun dtTex(path: String) = resources.getResource(DynamicTrees.resLoc("textures/$path.png")).inputStream.use { ImageIO.read(it) }
        fun mcTex(path: String) = resources.getResource(ResourceLocation("textures/$path.png")).inputStream.use { ImageIO.read(it) }
        
        val ironOreTex = mcTex("block/iron_ore")
        val oreOverlayTex = ironOreTex.filter {
            it.red > 160 && it.red > it.blue && it.green in (it.blue + 1) until it.red
        }
    
        val leavesTex = mcTex("block/oak_leaves")
        val baseLeavesTex = leavesTex.grayscale()
        
        val logTex = mcTex("block/oak_log")
        val logTopTex = mcTex("block/oak_log_top")
        val amberTex = texture("block/amber")
        val saplingTex = mcTex("block/oak_sapling")
        
        val resinTex = texture("item/resin")
        val oakSeedTex = dtTex("item/oak_seed")
        val seedOverlayTex = texture("item/seed_overlay")
        
        for (type in ConfigHandler.BAKED_COMMON.resources) {
            DynamicResourceTrees.LOGGER.log(Level.INFO, "Creating textures for type ${type.name}")
            
            DynamicResourceTrees.LOGGER.log(Level.DEBUG, "Creating leaves texture...")
            val leaves = baseLeavesTex.modify(type.color)
            map[DynamicResourceTrees.resourceLocation("textures/block/${type.name}_leaves")] = leaves.createTemp()
            
            DynamicResourceTrees.LOGGER.log(Level.DEBUG, "Creating log texture...")
            val log = logTex.modify(oreOverlayTex, type.color)
            map[DynamicResourceTrees.resourceLocation("textures/block/${type.name}_log")] = log.createTemp()
            
            DynamicResourceTrees.LOGGER.log(Level.DEBUG, "Creating log_top texture...")
            val logTop = logTopTex.modify(oreOverlayTex, type.color)
            map[DynamicResourceTrees.resourceLocation("textures/block/${type.name}_log_top")] = logTop.createTemp()
            
            DynamicResourceTrees.LOGGER.log(Level.DEBUG, "Creating amber texture...")
            val amber = amberTex.modify(type.color)
            map[DynamicResourceTrees.resourceLocation("textures/block/${type.name}_amber")] = amber.createTemp()
            
            DynamicResourceTrees.LOGGER.log(Level.DEBUG, "Creating resin texture...")
            val resin = resinTex.modify(type.color)
            map[DynamicResourceTrees.resourceLocation("textures/item/${type.name}_resin")] = resin.createTemp()
            
            DynamicResourceTrees.LOGGER.log(Level.DEBUG, "Creating seed texture...")
            val seed = oakSeedTex.modify(seedOverlayTex, type.color)
            map[DynamicResourceTrees.resourceLocation("textures/item/${type.name}_seed")] = seed.createTemp()
            
            DynamicResourceTrees.LOGGER.log(Level.DEBUG, "Creating sapling texture...")
            val sapling = saplingTex.modify(type.color)
            map[DynamicResourceTrees.resourceLocation("textures/block/${type.name}_sapling")] = sapling.createTemp()
        }
        
        DynamicResourceTrees.LOGGER.log(Level.INFO, "Created textures")
    }
    
    private fun generateTexture(namespace: String, path: String): Path? = synchronized(this) {
        return getCachedTextures()[ResourceLocation(namespace, path)]
    }
    
    override fun getResources(type: ResourcePackType, namespace: String, path: String, maxDepth: Int, filter: Predicate<String>): Collection<ResourceLocation> {
        if (type != packType) {
            return emptySet()
        }
        
        val list = mutableSetOf<ResourceLocation>()
        
        return list
    }
    
    override fun getNamespaces(type: ResourcePackType): Set<String> {
        if (type != packType) {
            return emptySet()
        }
        
        val namespaces = mutableSetOf<String>()
        
        namespaces.add(DynamicResourceTrees.MOD_ID)
        
        return namespaces
    }
    
    override fun <T> getMetadataSection(serializer: IMetadataSectionSerializer<T>): T? {
        return null
    }
    
    override fun getName(): String {
        return "DynamicResourceTrees dynamic resources"
    }
    
    override fun close() {
        cachedResources = null
        
        cachedTextures?.values?.forEach(Path::deleteIfExists)
        cachedTextures = null
    }
}