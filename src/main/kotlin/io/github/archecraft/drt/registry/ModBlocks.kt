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
    val REGISTRY = KDeferredRegister(ForgeRegistries.BLOCKS, DynamicResourceTrees.MOD_ID)
    
    fun configLoaded() {
        for (type in ConfigHandler.BAKED_COMMON.resources) {
            val leaves = LeavesBlock(Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn { _, _, _, _ -> false }.isSuffocating { _, _, _ -> false }.isViewBlocking { _, _, _ -> false })
            RegistryHandler.addBlock(DynamicResourceTrees.resourceLocation("${type.name}_leaves_v"), leaves)
            RegistryHandler.addItem(DynamicResourceTrees.resourceLocation("${type.name}_leaves_v"), BlockItem(leaves, Item.Properties()))
            
            val log = RotatedPillarBlock(Properties.of(Material.WOOD).strength(2.0F).sound(SoundType.WOOD))
            RegistryHandler.addBlock(DynamicResourceTrees.resourceLocation("${type.name}_log"), log)
            
            val sapling = SaplingBlock(object : Tree() {
                override fun getConfiguredFeature(p0: Random, p1: Boolean): ConfiguredFeature<BaseTreeFeatureConfig, *>? = null
            }, Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS))
            RegistryHandler.addBlock(DynamicResourceTrees.resourceLocation("${type.name}_sapling_v"), sapling)
            
            val resin = Item(Item.Properties())
            RegistryHandler.addItem(DynamicResourceTrees.resourceLocation("${type.name}_resin"), resin)
            
            val acorn = Item(Item.Properties())
            RegistryHandler.addItem(DynamicResourceTrees.resourceLocation("${type.name}_acorn"), acorn)
            
            val amber = Block(Properties.of(Material.GRASS).instabreak().speedFactor(0.9F))
            RegistryHandler.addBlock(DynamicResourceTrees.resourceLocation("${type.name}_amber"), amber)
            RegistryHandler.addItem(DynamicResourceTrees.resourceLocation("${type.name}_amber"), BlockItem(amber, Item.Properties()))
        }
    }
}