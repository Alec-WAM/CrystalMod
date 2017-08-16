package alec_wam.CrystalMod.entities.minions.worker;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.common.IPlantable;

public enum WorkerClass{
    NONE{
	    @Override
		boolean match(ItemStack item) {
			return true;
		}
    }, 
    HOE {
		@Override
		boolean match(ItemStack item) {
			for (ItemStack stack : Config.farmHoes) {
	          if (stack.getItem() == item.getItem()) {
	            return true;
	          }
			}
			return false;
		}
	}, 
	AXE  {
		@Override
		boolean match(ItemStack item) {
			return item.getItem().getHarvestLevel(item, "axe", null, null) >= 0;
		}
	}, 
	INTERACT  {
		@Override
		boolean match(ItemStack item) {
			return true;
		}
	},
	INTERACT_SEED  {
		@Override
		boolean match(ItemStack item) {
			return item.getItem() instanceof IPlantable;
		}
	},
	SHEARS {
		@Override
		boolean match(ItemStack item) {
			return item.getItem() instanceof ItemShears;
		}
	},	
	SWORD  {
		@Override
		boolean match(ItemStack item) {
			return item.getItem() instanceof ItemSword;
		}
	}; 
    
    public static WorkerClass getFromString(String s){
    	if(s.equalsIgnoreCase(HOE.name())){
    		return HOE;
    	}
    	if(s.equalsIgnoreCase(AXE.name())){
    		return AXE;
    	}
    	if(s.equalsIgnoreCase(SHEARS.name())){
    		return SHEARS;
    	}
    	if(s.equalsIgnoreCase(SWORD.name())){
    		return SWORD;
    	}
    	if(s.equalsIgnoreCase(INTERACT.name())){
    		return INTERACT;
    	}
    	if(s.equalsIgnoreCase(INTERACT_SEED.name())){
    		return INTERACT_SEED;
    	}
    	return NONE;
    }
    
    public boolean itemMatches(ItemStack item) {
      if (ItemStackTools.isNullStack(item)) {
        return this == INTERACT ? true : false;
      }
      return match(item) && !isBrokenTinkerTool(item);
    }

    private boolean isBrokenTinkerTool(ItemStack item)
    {
      return item.hasTagCompound() && item.getTagCompound().hasKey("InfiTool") && item.getTagCompound().getCompoundTag("InfiTool").getBoolean("Broken");
    }

    abstract boolean match(ItemStack item);

    public static List<WorkerClass> getValidTypes(){
    	List<WorkerClass> list = Lists.newArrayList();
    	list.add(HOE);
    	list.add(INTERACT);
    	list.add(SWORD);
    	list.add(SHEARS);
    	return list;
    }
    
    public static boolean isTool(ItemStack stack) {
      for (WorkerClass type : values()) {
        if (type.itemMatches(stack)) {
          return true;
        }
      }
      return false;
    }
}
