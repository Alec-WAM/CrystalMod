package alec_wam.CrystalMod.items.tools.backpack.types;

import java.util.UUID;

import com.google.common.base.Strings;

import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryBackpack implements IInventory {

	protected EntityPlayer player;
	protected ItemStack backpack = ItemStackTools.getEmptyStack();
	protected NonNullList<ItemStack> slots;
	protected int size;
	protected final String tagName;

    public InventoryBackpack(EntityPlayer player, ItemStack backpack, int size){
    	this(player, backpack, size, "");
    }
    
    public InventoryBackpack(EntityPlayer player, ItemStack backpack, int size, String tag){
    	this.size = size;
    	this.player = player;
    	this.backpack = backpack;
        this.slots = NonNullList.withSize(size, ItemStackTools.getEmptyStack());
        tagName = tag;
        readFromNBT(ItemNBTHelper.getCompound(backpack));
    }
    
    public InventoryBackpack(ItemStack backpack, int size){
    	this(backpack, size, "");
    }
    
    public InventoryBackpack(ItemStack backpack, int size, String tag){
    	this.size = size;
    	this.player = null;
    	this.backpack = backpack;
        this.slots = NonNullList.withSize(size, ItemStackTools.getEmptyStack());
        tagName = tag;
        readFromNBTNoPlayer(ItemNBTHelper.getCompound(backpack));
    }

    public EntityPlayer getPlayer() {
    	return player;
    }
    
    public ItemStack getBackpack(){
    	return backpack;
    }
    
    @Override
    public String getName(){
        return ItemStackTools.isValid(backpack) ? backpack.getDisplayName() : "backpack";
    }

    @Override
    public int getInventoryStackLimit(){
        return 64;
    }

    @Override
    public void markDirty(){

    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player){
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player){

    }

    @Override
    public void closeInventory(EntityPlayer player){

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack){
        return true;
    }

    @Override
    public int getField(int id){
        return 0;
    }

    @Override
    public void setField(int id, int value){

    }

    @Override
    public int getFieldCount(){
        return 0;
    }

    @Override
    public void clear(){
    	slots.clear();
    }

    @Override
    public boolean isEmpty(){
    	for(ItemStack stack : slots){
    		if(ItemStackTools.isValid(stack)){
    			return false;
    		}
    	}
    	return true;
    }
    
    @Override
    public void setInventorySlotContents(int i, ItemStack stack){
    	this.slots.set(i, stack);
        this.markDirty();
    }

    @Override
    public int getSizeInventory(){
        return size;
    }
    
    public int getSize(){
    	return size;
    }

    @Override
    public ItemStack getStackInSlot(int i){
        if(i < this.getSizeInventory()){
            return slots.get(i);
        }
        return ItemStackTools.getEmptyStack();
    }

    @Override
    public ItemStack decrStackSize(int i, int j){
        if(ItemStackTools.isValid(getStackInSlot(i))){
            ItemStack stackAt;
            if(ItemStackTools.getStackSize(getStackInSlot(i)) <= j){
                stackAt = getStackInSlot(i);
                setInventorySlotContents(i, ItemStackTools.getEmptyStack());
                this.markDirty();
                return stackAt;
            }
            else{
                stackAt = getStackInSlot(i).splitStack(j);
                if(ItemStackTools.isEmpty(getStackInSlot(i))){
                    setInventorySlotContents(i, ItemStackTools.getEmptyStack());
                }
                this.markDirty();
                return stackAt;
            }
        }
        return ItemStackTools.getEmptyStack();
    }

    @Override
    public ItemStack removeStackFromSlot(int index){
        ItemStack stack = getStackInSlot(index);
        setInventorySlotContents(index, ItemStackTools.getEmptyStack());
        return stack;
    }

    @Override
    public boolean hasCustomName(){
        return false;
    }


    @Override
    public ITextComponent getDisplayName(){
        return new TextComponentTranslation(getName());
    }
    
    public void guiSaveSafe(EntityPlayer player){
    	if(!player.getEntityWorld().isRemote) {
    		guiSave(player);
    	}
    	//Remove Open backpack or else it will just reopen the pack even though it is not equipped
    	ExtendedPlayerProvider.getExtendedPlayer(player).setOpenBackpack(ItemStackTools.getEmptyStack());
    }
    
    public void guiSave(EntityPlayer player){
    	if(ItemStackTools.isValid(backpack)){
    		save();
    	}
    }
    
    public void save(){
    	NBTTagCompound nbt = ItemNBTHelper.getCompound(backpack);
    	writeToNBT(nbt);
    	backpack.setTagCompound(nbt);
    }

    public void writeToNBT(NBTTagCompound nbt){
    	if(player == null || !player.getEntityWorld().isRemote) {
    		ItemStack found = player == null ? ItemStackTools.getEmptyStack() : findRealStack(player);
    		ItemStack backpackToUse = (ItemStackTools.isValid(found) ? found : backpack);
    		backpack = backpackToUse;
    		nbt = backpackToUse.getTagCompound();
    		if(!Strings.isNullOrEmpty(tagName)){
    			NBTTagCompound nbtInv = new NBTTagCompound();
    			ItemStackHelper.saveAllItems(nbtInv, slots);
    			nbt.setTag(tagName, nbtInv);
    		} else {
    			ItemUtil.writeInventoryToNBT(slots, nbt);
    		}
    	}
    }
    
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
    				ItemStackHelper.loadAllItems(nbt, slots);
    			}
    		}
    	}
    }
    
    public void readFromNBTNoPlayer(NBTTagCompound nbt){
    	if(ItemStackTools.isValid(backpack)){
			nbt = backpack.getTagCompound();
			if(!Strings.isNullOrEmpty(tagName)){
				nbt = backpack.getTagCompound().getCompoundTag(tagName);
			}
			if(nbt !=null){
				//Clear inventory before load
				clear();
				ItemStackHelper.loadAllItems(nbt, slots);
			}
		}
    }

	protected ItemStack findRealStack(EntityPlayer player) {
		if (ItemNBTHelper.hasUUID(backpack)){
            UUID parentUUID = ItemNBTHelper.getUUID(backpack);
            ItemStack found = BackpackUtil.findBackpack(player, parentUUID);
            return found;
        }
        return ItemStackTools.getEmptyStack();
	}
    
}
