package alec_wam.CrystalMod.items.tools.backpack.types;

import java.util.Arrays;

import com.google.common.base.Strings;

import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NormalInventoryBackpack extends InventoryBackpack {
	
	private ItemStack[] toolSlots;
	
	public NormalInventoryBackpack(ItemStack backpack, int size){
		super(backpack, size);
		toolSlots = new ItemStack[2];
		readFromNBTNoPlayer(ItemNBTHelper.getCompound(backpack));
	}
	
	public NormalInventoryBackpack(EntityPlayer player, ItemStack backpack, int size){
		super(player, backpack, size);
		toolSlots = new ItemStack[2];
		readFromNBT(ItemNBTHelper.getCompound(backpack));
    }
	
	@Override
	public int getSizeInventory()
    {
        return size+toolSlots.length;
    }
	
	@Override
	public ItemStack getStackInSlot(int index){
		if(index >=0 && index < size){
			return slots[index];
		}
		int toolIndex = index-size;
		return getToolStack(toolIndex);
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		if(index >=0 & index < size){
			slots[index] = stack;
			markDirty();
			return;
		} 
		
		int toolIndex = index-size;
		setToolStack(toolIndex, stack);
	}
	
	/**Weapon slot = 0, Tool slot = 1**/
	public ItemStack getToolStack(int index){
		if(index < 0 || index >= toolSlots.length)return ItemStackTools.getEmptyStack();
		return toolSlots[index];
	}
	
	public void setToolStack(int index, ItemStack stack){
		if(index < 0 || index >= toolSlots.length)return;
		toolSlots[index] = stack;
		markDirty();
	}
	
	@Override
    public void clear(){
        Arrays.fill(slots, ItemStackTools.getEmptyStack());
        if(toolSlots !=null)Arrays.fill(toolSlots, ItemStackTools.getEmptyStack());
    }

	public int getToolSize() {
		return toolSlots.length;
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
