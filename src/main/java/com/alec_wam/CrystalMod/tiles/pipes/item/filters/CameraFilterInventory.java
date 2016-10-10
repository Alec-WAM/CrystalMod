package com.alec_wam.CrystalMod.tiles.pipes.item.filters;

import java.util.List;

import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import com.alec_wam.CrystalMod.util.ItemUtil;
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

        if (masterStack != null)
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
                inventory.add(j, ItemStack.loadItemStackFromNBT(data));
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
        if (masterStack != null)
        {
            NBTTagCompound tag = ItemNBTHelper.getCompound(masterStack);
            readFromNBT(tag.getCompoundTag("filterInventory"));
        }
    }

    public void writeToStack(ItemStack masterStack)
    {
        if (masterStack != null)
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
        if (inventory.get(index) != null)
        {
//            if (!worldObj.isRemote)
//                worldObj.markBlockForUpdate(this.pos);

            if (inventory.get(index).stackSize <= count)
            {
                ItemStack itemStack = inventory.get(index);
                inventory.remove(index);
                markDirty();
                return itemStack;
            }

            ItemStack itemStack = inventory.get(index).splitStack(count);
            if (inventory.get(index).stackSize == 0)
            	inventory.remove(index);

            markDirty();
            return itemStack;
        }

        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int slot)
    {
        if (inventory.get(slot) != null)
        {
            ItemStack itemStack = inventory.get(slot);
            setInventorySlotContents(slot, null);
            return itemStack;
        }
        return null;
    }
    
    public void addItem(ItemStack stack){
    	if(stack !=null){
    		for(ItemStack stored : inventory){
    			if(ItemUtil.canCombine(stack, stored)){
    				return;
    			}
    		}
    		inventory.add(stack);
    		 if (stack != null && stack.stackSize > getInventoryStackLimit())
    	            stack.stackSize = getInventoryStackLimit();
    	        markDirty();
    	}
       
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
    	if(stack !=null)inventory.add(slot, stack);
    	else inventory.remove(slot);
        if (stack != null && stack.stackSize > getInventoryStackLimit())
            stack.stackSize = getInventoryStackLimit();
        markDirty();
//        if (!worldObj.isRemote)
//            worldObj.markBlockForUpdate(this.pos);
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
        if (masterStack != null)
        {
            this.writeToStack(masterStack);
        }
    }

    public boolean canInventoryBeManipulated()
    {
        return masterStack != null;
    }

	@Override
	public boolean hasGhostSlots() {
		return false;
	}
}
