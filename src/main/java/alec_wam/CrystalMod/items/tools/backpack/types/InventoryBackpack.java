package alec_wam.CrystalMod.items.tools.backpack.types;

import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryBackpack implements IInventory{

    public ItemStack[] slots;

    public InventoryBackpack(int size){
        this.slots = new ItemStack[size];
    }

    @Override
    public String getName(){
        return "backpack";
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
        int length = this.slots.length;
        this.slots = new ItemStack[length];
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack stack){
    	this.slots[i] = stack;
        this.markDirty();
    }

    @Override
    public int getSizeInventory(){
        return this.slots.length;
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
        return new TextComponentTranslation(this.getName());
    }
}
