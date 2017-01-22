package alec_wam.CrystalMod.items.tools.backpack.types;

import java.util.Arrays;

import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;
import joptsimple.internal.Strings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class NormalInventoryBackpack extends InventoryBackpack {
	
	private NonNullList<ItemStack> toolSlots;
	
	public NormalInventoryBackpack(ItemStack backpack, int size){
		super(backpack, size);
		toolSlots = NonNullList.withSize(size, ItemStackTools.getEmptyStack());
		readFromNBTNoPlayer(ItemNBTHelper.getCompound(backpack));
	}
	
	public NormalInventoryBackpack(EntityPlayer player, ItemStack backpack, int size){
		super(player, backpack, size);
		toolSlots = NonNullList.withSize(size, ItemStackTools.getEmptyStack());
		readFromNBT(ItemNBTHelper.getCompound(backpack));
    }
	
	@Override
	public int getSizeInventory()
    {
        return size+toolSlots.size();
    }
	
	@Override
	public ItemStack getStackInSlot(int index){
		if(index >=0 && index < size){
			return super.getStackInSlot(index);
		}
		int toolIndex = index-size;
		return getToolStack(toolIndex);
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		if(index >=0 & index < size){
			super.setInventorySlotContents(index, stack);
			return;
		} 
		
		int toolIndex = index-size;
		setToolStack(toolIndex, stack);
	}
	
	/**Weapon slot = 0, Tool slot = 1**/
	public ItemStack getToolStack(int index){
		if(index < 0 || index >= toolSlots.size())return ItemStackTools.getEmptyStack();
		return toolSlots.get(index);
	}
	
	public void setToolStack(int index, ItemStack stack){
		if(index < 0 || index >= toolSlots.size())return;
		toolSlots.set(index, stack);
		markDirty();
	}
	
	@Override
    public void clear(){
       if(slots !=null)slots.clear();
       if(toolSlots !=null)toolSlots.clear();
    }

	public int getToolSize() {
		return toolSlots.size();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
    	if(player == null || !player.getEntityWorld().isRemote) {
    		ItemStack found = player == null ? ItemStackTools.getEmptyStack() : findRealStack(player);
    		
    		ItemStack backpackToUse = (ItemStackTools.isValid(found) ? found : backpack);
    		backpack = backpackToUse;
    		nbt = backpackToUse.getTagCompound();
    		if(!Strings.isNullOrEmpty(tagName)){
    			NBTTagCompound nbtInv = new NBTTagCompound();
    			ItemUtil.writeInventoryToNBT(slots, nbtInv, "Items");
    			ItemUtil.writeInventoryToNBT(toolSlots, nbtInv, "Tools");
    			nbt.setTag(tagName, nbtInv);
    		} else {
    			ItemUtil.writeInventoryToNBT(slots, nbt, "Items");
    			ItemUtil.writeInventoryToNBT(toolSlots, nbt, "Tools");
    		}
    	}
    }
	
	@Override
    public void readFromNBT(NBTTagCompound nbt){
    	if(!player.getEntityWorld().isRemote){
    		ItemStack found = findRealStack(player);
    		backpack = (!ItemStackTools.isValid(found) ? backpack : found);
    		if(ItemStackTools.isValid(backpack)){
    			nbt = backpack.getTagCompound();
    			if(!Strings.isNullOrEmpty(tagName)){
    				nbt = backpack.getTagCompound().getCompoundTag(tagName);
    			}
    			if(nbt !=null){
    				//Clear inventory before load
    				clear();
    				ItemUtil.readInventoryFromNBT(slots, nbt, "Items");
    				if(toolSlots !=null)ItemUtil.readInventoryFromNBT(toolSlots, nbt, "Tools");
    			}
    		}
    	}
    }
    
    @Override
    public void readFromNBTNoPlayer(NBTTagCompound nbt){
    	if(ItemStackTools.isValid(backpack)){
			nbt = backpack.getTagCompound();
			if(!Strings.isNullOrEmpty(tagName)){
				nbt = backpack.getTagCompound().getCompoundTag(tagName);
			}
			if(nbt !=null){
				//Clear inventory before load
				clear();
				ItemUtil.readInventoryFromNBT(slots, nbt, "Items");
    			if(toolSlots !=null)ItemUtil.readInventoryFromNBT(toolSlots, nbt, "Tools");
			}
		}
    }
}
