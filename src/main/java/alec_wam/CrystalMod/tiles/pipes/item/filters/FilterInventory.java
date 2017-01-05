package alec_wam.CrystalMod.tiles.pipes.item.filters;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;

public class FilterInventory implements IItemStackInventory
{
    protected int[] syncedSlots = new int[0];
    private ItemStack[] inventory;
    private int size;
    private String name;
    protected ItemStack masterStack;

    public FilterInventory(ItemStack masterStack, int size, String name)
    {
        this.inventory = new ItemStack[size];
        this.size = size;
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

    private boolean isSyncedSlot(int slot)
    {
        for (int s : this.syncedSlots)
        {
            if (s == slot)
            {
                return true;
            }
        }
        return false;
    }

    public void readFromNBT(NBTTagCompound tagCompound)
    {
        NBTTagList tags = tagCompound.getTagList("Items", 10);
        inventory = new ItemStack[getSizeInventory()];

        for (int i = 0; i < tags.tagCount(); i++)
        {
            if (!isSyncedSlot(i))
            {
                NBTTagCompound data = tags.getCompoundTagAt(i);
                byte j = data.getByte("Slot");

                if (j >= 0 && j < inventory.length)
                {
                    inventory[j] = ItemStackTools.loadFromNBT(data);
                }
            }
        }
    }

    public void writeToNBT(NBTTagCompound tagCompound)
    {
        NBTTagList tags = new NBTTagList();

        for (int i = 0; i < inventory.length; i++)
        {
            if ((inventory[i] != null) && !isSyncedSlot(i))
            {
                NBTTagCompound data = new NBTTagCompound();
                data.setByte("Slot", (byte) i);
                inventory[i].writeToNBT(data);
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
        return size;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return inventory[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        if (!ItemStackTools.isNullStack(inventory[index]))
        {
            if (ItemStackTools.getStackSize(inventory[index]) <= count)
            {
                ItemStack itemStack = inventory[index];
                inventory[index] = ItemStackTools.getEmptyStack();
                markDirty();
                return itemStack;
            }

            ItemStack itemStack = inventory[index].splitStack(count);
            if (ItemStackTools.isEmpty(inventory[index]))
                inventory[index] = ItemStackTools.getEmptyStack();

            markDirty();
            return itemStack;
        }

        return ItemStackTools.getEmptyStack();
    }

    @Override
    public ItemStack removeStackFromSlot(int slot)
    {
        if (!ItemStackTools.isNullStack(inventory[slot]))
        {
            ItemStack itemStack = inventory[slot];
            setInventorySlotContents(slot, ItemStackTools.getEmptyStack());
            return itemStack;
        }
        return ItemStackTools.getEmptyStack();
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        inventory[slot] = stack;
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
        this.inventory = new ItemStack[size];
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
		return true;
	}
	
	public ItemStack getMasterStack(){
		return masterStack;
	}
}
