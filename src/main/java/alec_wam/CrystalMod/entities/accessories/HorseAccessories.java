package alec_wam.CrystalMod.entities.accessories;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.Util;

public class HorseAccessories {
    public static String NBT_ACCESSORY_HORSE_ENDERCHEST = "EnderChest";
    
    public static boolean handleHorseInteract(EntityPlayer player, ItemStack held, EntityHorse horse){
    	if(handleEnderChestInteract(horse, held, player))return true;
    	return false;
    }

    private static boolean handleEnderChestInteract(EntityHorse horse, ItemStack held, EntityPlayer player){
    	if(hasEnderChest(horse)){
    		if(held == null){
    			if(!player.isSneaking())return false;
	  			InventoryEnderChest inventoryenderchest = player.getInventoryEnderChest();
	  			if(inventoryenderchest !=null){
	  				if(player.worldObj.isRemote){
	  					return true;
	  				}
	  				player.displayGUIChest(inventoryenderchest);
	  				return true;
	  			}
  		  	} else {
  		  		Block block = Block.getBlockFromItem(held.getItem());
  		  		boolean isChest = block !=null && Block.getBlockFromItem(held.getItem()) instanceof BlockChest;
  		  		if(isChest){
  		  			boolean success = removeEnderChest(horse);
  		  			if(success) {
	  		  			if(player.worldObj.isRemote){
		  					return true;
		  				}
		  		  		horse.dropItem(Item.getItemFromBlock(Blocks.ENDER_CHEST), 1);
		  		  		return true;
  		  			}
  		  		}
  		  	}
    	} else {
    		if(held !=null){
	    		Block block = Block.getBlockFromItem(held.getItem());
	    		if(block !=null && block == Blocks.ENDER_CHEST){
	    			boolean success = addEnderChest(horse);
		  			if(success) {
		  				if(player.worldObj.isRemote){
		  					return true;
		  				}
		  				if(!player.capabilities.isCreativeMode){
		  					held.stackSize--;
		  				}
		  				return true;
		  			}
	    		}
    		}
    	}
    	return false;
    }
    
	public static void onHorseDeath(EntityHorse horse) {
		if(hasEnderChest(horse))horse.dropItem(Item.getItemFromBlock(Blocks.ENDER_CHEST), 1);
	}
    
    public static boolean hasEnderChest(EntityHorse horse){
    	NBTTagCompound nbt = Util.getCustomEntityData(horse, false);
    	if(nbt == null)return false;
    	boolean hasEnderChest = nbt.getBoolean(NBT_ACCESSORY_HORSE_ENDERCHEST);
		return hasEnderChest;
    }
    
    public static void setHasEnderChest(EntityHorse horse, boolean value){
    	NBTTagCompound nbt = Util.getCustomEntityData(horse, true);
    	nbt.setBoolean(NBT_ACCESSORY_HORSE_ENDERCHEST, value);
    }
    
    public static boolean addEnderChest(EntityHorse horse){
    	if(horse == null 
		|| !(horse.getType() != null && horse.getType().canBeChested() && !horse.isChested() && horse.isAdultHorse()) 
		|| hasEnderChest(horse)) return false;
    	
    	setHasEnderChest(horse, true);
    	return true;
    }
    
    public static boolean removeEnderChest(EntityHorse horse){
    	if(horse == null || !hasEnderChest(horse)) return false;
    	setHasEnderChest(horse, false);
    	return true;
    }
	
}