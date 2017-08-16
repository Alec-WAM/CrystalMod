package alec_wam.CrystalMod.tiles.pipes.item.filters;

import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class FilterInventory implements IItemStackInventory
{
    protected int[] syncedSlots = new int[0];
    private NonNullList<ItemStack> inventory;
    private int size;
    private String name;
    protected ItemStack masterStack;

    public FilterInventory(ItemStack masterStack, int size, String name)
    {
    	inventory = NonNullList.<ItemStack>withSize(size, ItemStack.EMPTY);
        this.size = size;
        this.name = name;
        this.masterStack = masterStack;

        if (!ItemStackTools.isNullStack(masterStack))
            this.readFromStack(masterStack);
    }

    @Override
	public void initializeInventory(ItemStack masterStack)
    {
        this.masterStack = masterStack;
        this.clear();
        this.readFromStack(masterStack);
    }

    public void readFromNBT(NBTTagCompound tagCompound)
    {
        ItemStackHelper.loadAllItems(tagCompound, inventory);
    }

    public void writeToNBT(NBTTagCompound tagCompound)
    {
        ItemStackHelper.saveAllItems(tagCompound, inventory);
    }

    public void readFromStack(ItemStack masterStack)
    {
        if (!ItemStackTools.isNullStack(masterStack))
        {
            NBTTagCompound tag = ItemNBTHelper.getCompound(masterStack);
            readFromNBT(tag.getCompoundTag("filterInventory"));
        }
    }

    public void writeToStack(ItemStack masterStack)
    {
        if (!ItemStackTools.isNullStack(masterStack))
        {
            NBTTagCompound tag = ItemNBTHelper.getCompound(masterStack);
            NBTTagCompound invTag = new NBTTagCompound();
            writeToNBT(invTag);
            tag.setTag("filterInventory", invTag);
        }
    }

    @Override
    public int getSizeInventory()
    {
        return size;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        if (!ItemStackTools.isNullStack(getStackInSlot(index)))
        {
            if (ItemStackTools.getStackSize(getStackInSlot(index)) <= count)
            {
                ItemStack itemStack = getStackInSlot(index);
                setInventorySlotContents(index, ItemStackTools.getEmptyStack());
                markDirty();
                return itemStack;
            }

            ItemStack itemStack = getStackInSlot(index).splitStack(count);
            if (ItemStackTools.isEmpty(getStackInSlot(index)))
            	setInventorySlotContents(index, ItemStackTools.getEmptyStack());

            markDirty();
            return itemStack;
        }

        return ItemStackTools.getEmptyStack();
    }

    @Override
    public ItemStack removeStackFromSlot(int slot)
    {
        if (!ItemStackTools.isNullStack(getStackInSlot(slot)))
        {
            ItemStack itemStack = getStackInSlot(slot);
            setInventorySlotContents(slot, ItemStackTools.getEmptyStack());
            return itemStack;
        }
        return ItemStackTools.getEmptyStack();
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        inventory.set(slot, stack);
        if (!ItemStackTools.isNullStack(stack) && ItemStackTools.getStackSize(stack) > getInventoryStackLimit())
        	ItemStackTools.setStackSize(stack, getInventoryStackLimit());
        markDirty();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player)
    {

    }

    @Override
    public void closeInventory(EntityPlayer player)
    {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public void setField(int id, int value)
    {

    }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void clear()
    {
        this.inventory.clear();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentString(getName());
    }

    @Override
    public void markDirty()
    {
        if (!ItemStackTools.isNullStack(masterStack))
        {
            this.writeToStack(masterStack);
        }
    }

    @Override
	public boolean canInventoryBeManipulated()
    {
        return !ItemStackTools.isNullStack(masterStack);
    }

	@Override
	public boolean hasGhostSlots() {
		return true;
	}
	
	public ItemStack getMasterStack(){
		return masterStack;
	}
	
	@Override
	public boolean isEmpty(){
		for(ItemStack stack : inventory){
			if(ItemStackTools.isValid(stack)){
				return false;
			}
		}
		return true;
	}
}
