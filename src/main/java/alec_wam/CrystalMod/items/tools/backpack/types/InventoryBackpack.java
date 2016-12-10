package alec_wam.CrystalMod.items.tools.backpack.types;

import java.util.UUID;

import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryBackpack implements IInventory {

	private EntityPlayer player;
	private ItemStack backpack;
	public ItemStack[] slots;
	private int size;

    public InventoryBackpack(EntityPlayer player, ItemStack backpack, int size){
    	this.size = size;
    	this.player = player;
    	this.backpack = backpack;
        this.slots = new ItemStack[getSizeInventory()];
        readFromNBT(ItemNBTHelper.getCompound(backpack));
    }
    
    public InventoryBackpack(ItemStack backpack, int size){
    	this.size = size;
    	this.player = null;
    	this.backpack = backpack;
        this.slots = new ItemStack[getSizeInventory()];
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
    public boolean isUseableByPlayer(EntityPlayer player){
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
        this.slots = new ItemStack[getSizeInventory()];
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack stack){
    	this.slots[i] = stack;
        this.markDirty();
    }

    @Override
    public int getSizeInventory(){
        return size;
    }

    @Override
    public ItemStack getStackInSlot(int i){
        if(i < this.getSizeInventory()){
            return this.slots[i];
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int i, int j){
        if(!ItemStackTools.isNullStack(slots[i])){
            ItemStack stackAt;
            if(ItemStackTools.getStackSize(slots[i]) <= j){
                stackAt = this.slots[i];
                this.slots[i] = ItemStackTools.getEmptyStack();
                this.markDirty();
                return stackAt;
            }
            else{
                stackAt = this.slots[i].splitStack(j);
                if(ItemStackTools.isEmpty(this.slots[i])){
                    this.slots[i] = ItemStackTools.getEmptyStack();
                }
                this.markDirty();
                return stackAt;
            }
        }
        return ItemStackTools.getEmptyStack();
    }

    @Override
    public ItemStack removeStackFromSlot(int index){
        ItemStack stack = this.slots[index];
        this.slots[index] = ItemStackTools.getEmptyStack();
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
    	if(!player.getEntityWorld().isRemote) {
    		ItemStack found = findRealStack(player);
    		ItemStack backpackToUse = (!ItemStackTools.isValid(found) ? backpack : found);
    		
    		nbt = backpackToUse.getTagCompound();
    		ItemUtil.writeInventoryToNBT(this, nbt);
    	}
    }
    
    public void readFromNBT(NBTTagCompound nbt){
    	if(!player.getEntityWorld().isRemote){
    		ItemStack found = findRealStack(player);
    		backpack = (!ItemStackTools.isValid(found) ? backpack : found);
    		if(ItemStackTools.isValid(backpack)){
    			nbt = backpack.getTagCompound();
    			if(nbt !=null){
    				//Clear inventory before load
    				clear();
    				ItemUtil.readInventoryFromNBT(this, nbt);
    			}
    		}
    	}
    }
    
    public void readFromNBTNoPlayer(NBTTagCompound nbt){
    	if(ItemStackTools.isValid(backpack)){
			nbt = backpack.getTagCompound();
			if(nbt !=null){
				//Clear inventory before load
				clear();
				ItemUtil.readInventoryFromNBT(this, nbt);
			}
		}
    }

	private ItemStack findRealStack(EntityPlayer player) {
		if (ItemNBTHelper.hasUUID(backpack)){
            UUID parentUUID = ItemNBTHelper.getUUID(backpack);
            ItemStack found = BackpackUtil.findBackpack(player, parentUUID);
            return found;
        }
        return ItemStackTools.getEmptyStack();
	}
    
}
