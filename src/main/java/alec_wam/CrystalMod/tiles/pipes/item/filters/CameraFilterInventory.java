package alec_wam.CrystalMod.tiles.pipes.item.filters;

import java.util.List;

import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class CameraFilterInventory implements IItemStackInventory
{
    private List<ItemStack> inventory;
    private String name;
    protected ItemStack masterStack;

    public CameraFilterInventory(ItemStack masterStack, String name)
    {
        this.inventory = Lists.newArrayList();
        this.name = name;
        this.masterStack = masterStack;

        if (!ItemStackTools.isNullStack(masterStack))
            this.readFromStack(masterStack);
    }

    public void initializeInventory(ItemStack masterStack)
    {
        this.masterStack = masterStack;
        this.clear();
        this.readFromStack(masterStack);
    }

    public void readFromNBT(NBTTagCompound tagCompound)
    {
        NBTTagList tags = tagCompound.getTagList("Items", 10);
        inventory = Lists.newArrayList();

        for (int i = 0; i < tags.tagCount(); i++)
        {
            NBTTagCompound data = tags.getCompoundTagAt(i);
            byte j = data.getByte("Slot");

            if (j >= 0)
            {
                inventory.add(j, ItemStackTools.loadFromNBT(data));
            }
        }
    }

    public void writeToNBT(NBTTagCompound tagCompound)
    {
        NBTTagList tags = new NBTTagList();

        for (int i = 0; i < inventory.size(); i++)
        {
            if ((inventory.get(i) != null))
            {
                NBTTagCompound data = new NBTTagCompound();
                data.setByte("Slot", (byte) i);
                inventory.get(i).writeToNBT(data);
                tags.appendTag(data);
            }
        }

        tagCompound.setTag("Items", tags);
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
        return inventory.size();
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        if (!ItemStackTools.isNullStack(inventory.get(index)))
        {
        	if (ItemStackTools.getStackSize(inventory.get(index)) <= count)
            {
                ItemStack itemStack = inventory.get(index);
                inventory.remove(index);
                markDirty();
                return itemStack;
            }

            ItemStack itemStack = inventory.get(index).splitStack(count);
            if (ItemStackTools.isEmpty(inventory.get(index)))
            	inventory.remove(index);

            markDirty();
            return itemStack;
        }

        return ItemStackTools.getEmptyStack();
    }

    @Override
    public ItemStack removeStackFromSlot(int slot)
    {
        if (!ItemStackTools.isNullStack(inventory.get(slot)))
        {
            ItemStack itemStack = inventory.get(slot);
            setInventorySlotContents(slot, ItemStackTools.getEmptyStack());
            return itemStack;
        }
        return ItemStackTools.getEmptyStack();
    }
    
    public void addItem(ItemStack stack){
    	if(!ItemStackTools.isNullStack(stack)){
    		for(ItemStack stored : inventory){
    			if(ItemUtil.canCombine(stack, stored)){
    				return;
    			}
    		}
    		inventory.add(stack);
    		if (!ItemStackTools.isNullStack(stack) && ItemStackTools.getStackSize(stack) > getInventoryStackLimit()){
	            ItemStackTools.setStackSize(stack, getInventoryStackLimit());
    		}
	        markDirty();
    	}
       
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
    	if(!ItemStackTools.isNullStack(stack))inventory.add(slot, stack);
    	else inventory.remove(slot);
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
    public boolean isUseableByPlayer(EntityPlayer player)
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
        this.inventory = Lists.newArrayList();
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

    public boolean canInventoryBeManipulated()
    {
        return !ItemStackTools.isNullStack(masterStack);
    }

	@Override
	public boolean hasGhostSlots() {
		return false;
	}
}
