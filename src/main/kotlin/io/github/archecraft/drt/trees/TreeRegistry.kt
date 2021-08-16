package io.github.archecraft.drt.trees

import com.ferreusveritas.dynamictrees.api.registry.*
import com.ferreusveritas.dynamictrees.resources.DTResourceRegistries.*
import com.ferreusveritas.dynamictrees.systems.dropcreators.*
import com.ferreusveritas.dynamictrees.systems.dropcreators.context.*
import io.github.archecraft.drt.*
import net.minecraft.item.*
import net.minecraftforge.client.event.*
import net.minecraftforge.registries.*
import org.apache.logging.log4j.*

object TreeRegistry {
    fun onDropCreatorRegistry(event: RegistryEvent<DropCreator>) {
        event.registry.register(ResinDrops)
    }
    
    
    fun onTreeManager(event: AddTreesLoadListenerEvent) {
        event.treesResourceManager.addResourcePack(DRTTreesPack())
    }
    
    
    object ResinDrops : DropCreator(DynamicResourceTrees.resourceLocation("resin_drops")) {
        override fun registerProperties() {
        }
        
        override fun appendLogDrops(configuration: ConfiguredDropCreator<DropCreator>, context: LogDropContext) {
            val species = context.species().registryName
            val logsAndSticks = context.species().getLogsAndSticks(context.volume())
            context.drops().add(ItemStack(ForgeRegistries.ITEMS.getValue(DynamicResourceTrees.resourceLocation(species.path + "_resin")) ?: run {
                DynamicResourceTrees.LOGGER.log(Level.ERROR, "Failed to get item for ${species.path}_resin")
                return
            }, logsAndSticks.logs.sumOf { it.count }))
        }
        
        override fun appendHarvestDrops(configuration: ConfiguredDropCreator<DropCreator>, context: DropContext) {
            appendLeafDrops(context, 16)
        }
        
        override fun appendLeavesDrops(configuration: ConfiguredDropCreator<DropCreator>, context: DropContext) {
            appendLeafDrops(context, 8)
        }
        
        private fun appendLeafDrops(context: DropContext, chance: Int) {
            val species = context.species().registryName
            
            if (context.random().nextInt(chance) == 0) {
                context.drops().add(ItemStack(ForgeRegistries.ITEMS.getValue(DynamicResourceTrees.resourceLocation(species.path + "_seed")) ?: run {
                    DynamicResourceTrees.LOGGER.log(Level.ERROR, "Failed to get item for ${species.path}_acorn")
                    return
                }, 1))
            }
        }
    }
}