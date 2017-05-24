package alec_wam.CrystalMod.api.enhancements;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnhancementManager {

	private static Map<ResourceLocation, IEnhancement> ENHANCEMENT_REGISTRY = Maps.newHashMap();
	
	public static boolean register(IEnhancement enhancement){
		if(ENHANCEMENT_REGISTRY.containsKey(enhancement.getID())) return false;
		ENHANCEMENT_REGISTRY.put(enhancement.getID(), enhancement);
		return true;
	}
	
	public static IEnhancement getEnhancement(ResourceLocation id){
		return ENHANCEMENT_REGISTRY.get(id);
	}
	
	public static List<IEnhancement> findValidEnhancements(ItemStack stack){
		List<IEnhancement> list = Lists.newArrayList();
		if(ItemStackTools.isValid(stack)){
			for(IEnhancement enhancement : ENHANCEMENT_REGISTRY.values()){
				if(enhancement.canApply(stack)){
					list.add(enhancement);
				}
			}
		}
		return list;		
	}
	
	public static List<IEnhancement> getAppliedEnhancements(ItemStack stack){
		List<IEnhancement> list = Lists.newArrayList();
		if(ItemStackTools.isValid(stack)){
			for(IEnhancement enhancement : ENHANCEMENT_REGISTRY.values()){
				if(enhancement.isApplied(stack)){
					list.add(enhancement);
				}
			}
		}
		return list;
	}
	
}
