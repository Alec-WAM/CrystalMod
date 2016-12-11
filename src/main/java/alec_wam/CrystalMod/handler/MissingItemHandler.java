package alec_wam.CrystalMod.handler;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MissingItemHandler {

	public static final Map<ResourceLocation, Item> remapItems = Maps.newHashMap();
	public static final Map<ResourceLocation, Block> remapBlocks = Maps.newHashMap();

	public static void missingFix(FMLMissingMappingsEvent event){
		List<MissingMapping> missing = event.getAll();
		for(MissingMapping mapping : missing){
			if(mapping.resourceLocation.getResourceDomain().equalsIgnoreCase(CrystalMod.MODID)){
				ModLogger.warning("Found missing block/item ["+mapping.resourceLocation+"]!");
				final ResourceLocation rl = mapping.resourceLocation;
				
				boolean fixed = false;
				
				//Hardcoded
				if(mapping.resourceLocation.getResourcePath().equalsIgnoreCase("backpacklock")){
					mapping.remap(ModItems.lock);
					ModLogger.info("Fixed "+rl+". Remapped it to "+ModItems.lock.getRegistryName());
					fixed = true;
				} 
				if(mapping.type == GameRegistry.Type.ITEM){
					ModLogger.info(remapItems.toString());
					if(remapItems.containsKey(rl)){
						Item item = remapItems.get(rl);
						mapping.remap(item);
						ModLogger.info("Fixed "+rl+". Remapped it to "+item.getRegistryName());
						fixed = true;
					}
				} 
				if(mapping.type == GameRegistry.Type.BLOCK){
					if(remapBlocks.containsKey(rl)){
						Block block = remapBlocks.get(rl);
						mapping.remap(block);
						ModLogger.info("Fixed "+rl+". Remapped it to "+block.getRegistryName());
						fixed = true;
					}
				}
				if(!fixed){
					ModLogger.warning("Warning! "+rl+" was not remapped. Continue with caution.");
					//throw new RuntimeException("Warning "+rl+" needs to be remapped.");
				}
			}
		}
	}
}
