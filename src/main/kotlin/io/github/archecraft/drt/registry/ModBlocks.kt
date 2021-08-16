package io.github.archecraft.drt.registry

import com.ferreusveritas.dynamictrees.api.registry.*
import io.github.archecraft.drt.*
import io.github.archecraft.drt.config.*
import net.minecraft.block.*
import net.minecraft.block.AbstractBlock.Properties
import net.minecraft.block.material.*
import net.minecraft.block.trees.*
import net.minecraft.item.*
import net.minecraft.world.gen.feature.*
import net.minecraftforge.registries.*
import thedarkcolour.kotlinforforge.forge.*
import java.util.*

object ModBlocks {
    object Blocks {
        val leaves = mutableMapOf<ResourceType, Block>()
        val log = mutableMapOf<ResourceType, Block>()
        val sapling = mutableMapOf<ResourceType, Block>()
        val amber = mutableMapOf<ResourceType, Block>()
    }
    
    object Items {
        val leaves = mutableMapOf<ResourceType, Item>()
        val resin = mutableMapOf<ResourceType, Item>()
        val amber = mutableMapOf<ResourceType, Item>()
    }
    
    
    fun configLoaded() {
        for (type in ConfigHandler.BAKED_COMMON.resources) {
            Blocks.leaves[type] = LeavesBlock(Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn { _, _, _, _ -> false }.isSuffocating { _, _, _ -> false }.isViewBlocking { _, _, _ -> false })
            Items.leaves[type] = BlockItem(Blocks.leaves[type]!!, Item.Properties())
            RegistryHandler.addBlock(DynamicResourceTrees.resourceLocation("${type.name}_leaves_v"), Blocks.leaves[type]!!)
            RegistryHandler.addItem(DynamicResourceTrees.resourceLocation("${type.name}_leaves_v"), Items.leaves[type]!!)
            
            Blocks.log[type] = RotatedPillarBlock(Properties.of(Material.WOOD).strength(2.0F).sound(SoundType.WOOD))
            RegistryHandler.addBlock(DynamicResourceTrees.resourceLocation("${type.name}_log"), Blocks.log[type]!!)
            
            Blocks.sapling[type] = SaplingBlock(object : Tree() {
                override fun getConfiguredFeature(p0: Random, p1: Boolean): ConfiguredFeature<BaseTreeFeatureConfig, *>? = null
            }, Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS))
            RegistryHandler.addBlock(DynamicResourceTrees.resourceLocation("${type.name}_sapling_v"), Blocks.sapling[type]!!)
            
            Items.resin[type] = Item(Item.Properties())
            RegistryHandler.addItem(DynamicResourceTrees.resourceLocation("${type.name}_resin"), Items.resin[type]!!)
            
            Blocks.amber[type] = Block(Properties.of(Material.GRASS).instabreak().speedFactor(0.9F))
            Items.amber[type] = BlockItem(Blocks.amber[type]!!, Item.Properties())
            RegistryHandler.addBlock(DynamicResourceTrees.resourceLocation("${type.name}_amber"), Blocks.amber[type]!!)
            RegistryHandler.addItem(DynamicResourceTrees.resourceLocation("${type.name}_amber"), Items.amber[type]!!)
        }
    }
}