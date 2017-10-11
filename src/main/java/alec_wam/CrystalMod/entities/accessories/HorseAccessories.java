package alec_wam.CrystalMod.entities.accessories;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.asm.ObfuscatedNames;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ReflectionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

public class HorseAccessories {
    //EnderChest stuff
	public static String NBT_ACCESSORY_HORSE_ENDERCHEST = "EnderChest";
    
    public static boolean handleHorseInteract(EntityPlayer player, ItemStack held, EnumHand hand, AbstractHorse horse){
    	if(handleEnderChestInteract(horse, held, hand, player))return true;
    	return false;
    }

    private static boolean handleEnderChestInteract(AbstractHorse horse, ItemStack held, EnumHand hand, EntityPlayer player){
    	if(hasEnderChest(horse)){
    		if(ItemStackTools.isNullStack(held)){
    			if(!player.isSneaking())return false;
	  			InventoryEnderChest inventoryenderchest = player.getInventoryEnderChest();
	  			if(inventoryenderchest !=null){
	  				if(player.getEntityWorld().isRemote){
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
	  		  			if(player.getEntityWorld().isRemote){
		  					return true;
		  				}
		  		  		horse.dropItem(Item.getItemFromBlock(Blocks.ENDER_CHEST), 1);
		  		  		return true;
  		  			}
  		  		}
  		  	}
    	} else {
    		if(!ItemStackTools.isNullStack(held)){
	    		Block block = Block.getBlockFromItem(held.getItem());
	    		if(block !=null && block == Blocks.ENDER_CHEST){
	    			boolean success = addEnderChest(horse);
		  			if(success) {
		  				if(player.getEntityWorld().isRemote){
		  					return true;
		  				}
		  				if(!player.capabilities.isCreativeMode){
		  					ItemStackTools.incStackSize(held, -1);
		  				}
		  				return true;
		  			}
	    		}
    		}
    	}
    	return false;
    }
    
    public static ContainerHorseChest getHorseChest(AbstractHorse horse){
    	try{
    		Object obj = ReflectionUtils.getPrivateValue(horse, AbstractHorse.class, ObfuscatedNames.AbstractHorse_horseChest);
    		if(obj !=null && obj instanceof ContainerHorseChest){
    			return (ContainerHorseChest)obj;
    		}
    	} catch(Exception e){
    		throw new RuntimeException("Error when trying to get the Horses chest check MCP mapping");
    	}
    	return null;
    }
    
	public static void onHorseDeath(AbstractHorse horse) {
		if(hasEnderChest(horse)){
			horse.dropItem(Item.getItemFromBlock(Blocks.ENDER_CHEST), 1);
		}
	}
    
    public static boolean hasEnderChest(AbstractHorse horse){
    	NBTTagCompound nbt = EntityUtil.getCustomEntityData(horse);
    	return nbt.hasKey(NBT_ACCESSORY_HORSE_ENDERCHEST) && nbt.getBoolean(NBT_ACCESSORY_HORSE_ENDERCHEST);
    }
    
    public static void setHasEnderChest(AbstractHorse horse, boolean value){
    	NBTTagCompound nbt = EntityUtil.getCustomEntityData(horse);
    	nbt.setBoolean(NBT_ACCESSORY_HORSE_ENDERCHEST, value);
    	EntityUtil.setCustomEntityData(horse, nbt);
    }
    
    public static boolean addEnderChest(AbstractHorse horse){
    	if(horse == null || !(horse instanceof AbstractChestHorse) || horse.getGrowingAge() != 0 
		|| hasEnderChest(horse)) return false;
    	
    	AbstractChestHorse horseChest = (AbstractChestHorse)horse;
    	
    	if(horseChest.hasChest()){
    		ContainerHorseChest chest = getHorseChest(horse);
    		if (!horse.world.isRemote && chest != null)
            {
                for (int i = 0; i < chest.getSizeInventory(); ++i)
                {
                    ItemStack itemstack = chest.getStackInSlot(i);

                    if (!itemstack.isEmpty())
                    {
                        horse.entityDropItem(itemstack, 0.0F);
                    }
                }
            }
    	}
    	
    	setHasEnderChest(horse, true);
    	return true;
    }
    
    public static boolean removeEnderChest(AbstractHorse horse){
    	if(horse == null || !hasEnderChest(horse)) return false;
    	setHasEnderChest(horse, false);
    	return true;
    }
    
    //Horse Shoe Stuff
    public static String NBT_ACCESSORY_HORSE_HORSESHOES = "HorseShoes";

    private static boolean handleHorseShoeInteract(AbstractHorse horse, ItemStack held, EnumHand hand, EntityPlayer player){
    	//TODO Finish this
    	if(ItemStackTools.isValid(getHorseShoes(horse))){
    		if(ItemStackTools.isNullStack(held)){
    			if(!player.isSneaking())return false;
	  			InventoryEnderChest inventoryenderchest = player.getInventoryEnderChest();
	  			if(inventoryenderchest !=null){
	  				if(player.getEntityWorld().isRemote){
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
	  		  			if(player.getEntityWorld().isRemote){
		  					return true;
		  				}
		  		  		horse.dropItem(Item.getItemFromBlock(Blocks.ENDER_CHEST), 1);
		  		  		return true;
  		  			}
  		  		}
  		  	}
    	} else {
    		if(ItemStackTools.isValid(held)){
	    		if(held.getItem() == ModItems.horseShoes){
	    			boolean success = addHorseShoes(horse, held);
		  			if(success) {
		  				if(player.getEntityWorld().isRemote){
		  					return true;
		  				}
		  				if(!player.capabilities.isCreativeMode){
		  					ItemStackTools.incStackSize(held, -1);
		  				}
		  				return true;
		  			}
	    		}
    		}
    	}
    	return false;
    }
    
    public static ItemStack getHorseShoes(AbstractHorse horse){
    	NBTTagCompound nbt = EntityUtil.getCustomEntityData(horse);
    	if(nbt.hasKey(NBT_ACCESSORY_HORSE_HORSESHOES)){
    		ItemStack shoes = ItemStackTools.loadFromNBT(nbt.getCompoundTag(NBT_ACCESSORY_HORSE_HORSESHOES));
    		return ItemStackTools.isValid(shoes) ? shoes : ItemStackTools.getEmptyStack();
    	}
    	return ItemStackTools.getEmptyStack();
    }
    
    public static void setHorseShoes(AbstractHorse horse, @Nonnull ItemStack shoes){
    	NBTTagCompound nbt = EntityUtil.getCustomEntityData(horse);
    	if(ItemStackTools.isEmpty(shoes)){
    		nbt.removeTag(NBT_ACCESSORY_HORSE_HORSESHOES);
    	} else {
    		nbt.setTag(NBT_ACCESSORY_HORSE_HORSESHOES, shoes.writeToNBT(new NBTTagCompound()));
    	}
    	EntityUtil.setCustomEntityData(horse, nbt);
    }
    
    public static boolean addHorseShoes(AbstractHorse horse, ItemStack shoes){
    	if(horse == null || horse.getGrowingAge() != 0 || ItemStackTools.isValid(getHorseShoes(horse))) return false;
    	
    	setHorseShoes(horse, shoes);
    	return true;
    }
    
    public static ItemStack removeHorseShoes(AbstractHorse horse){
    	if(horse == null || ItemStackTools.isEmpty(getHorseShoes(horse))) return ItemStackTools.getEmptyStack();
    	ItemStack shoes = getHorseShoes(horse);
    	setHorseShoes(horse, ItemStackTools.getEmptyStack());
    	return shoes;
    }
	
}
