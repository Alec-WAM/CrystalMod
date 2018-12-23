package alec_wam.CrystalMod.entities.minions.warrior;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryWarrior implements IInventory
{
	public static final int MAIN_SIZE = 3;
	
	public NonNullList<ItemStack> mainInventory = NonNullList.<ItemStack>withSize(MAIN_SIZE, ItemStack.EMPTY);
    
    public int currentItem;
    public EntityMinionWarrior minion;
    public boolean inventoryChanged;

    public InventoryWarrior(EntityMinionWarrior minionIn)
    {
        this.minion = minionIn;
    }
    
    public void updateDisplayItems(){
    	if(minion.getSlotSelected() == -1){
    		minion.setHeldItem(EnumHand.MAIN_HAND, ItemStackTools.getEmptyStack());
    	} else {
    		minion.setHeldItem(EnumHand.MAIN_HAND, getStackInSlot(minion.getSlotSelected()));
    	}
    	if(minion.getBackSelected() == -1){
    		minion.setBackItem(ItemStackTools.getEmptyStack());
    	} else {
    		minion.setBackItem(getStackInSlot(minion.getBackSelected()));
    	}
    }
    
    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    @Override
	public ItemStack decrStackSize(int index, int count)
    {
    	ItemStack stack = this.mainInventory.get(index);
        if (ItemStackTools.isValid(stack))
        {
            if (ItemStackTools.getStackSize(stack) <= count)
            {
                ItemStack itemstack1 = stack;
                this.mainInventory.set(index, ItemStackTools.getEmptyStack());
                updateDisplayItems();
                return itemstack1;
            }
            else
            {
                ItemStack itemstack = stack.splitStack(count);

                if (ItemStackTools.isEmpty(itemstack))
                {
                	this.mainInventory.set(index, ItemStackTools.getEmptyStack());
                }
                updateDisplayItems();
                return itemstack;
            }
        }
        else
        {
            return ItemStackTools.getEmptyStack();
        }
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    @Override
	public ItemStack removeStackFromSlot(int index)
    {
    	if (ItemStackTools.isValid(this.mainInventory.get(index)))
        {
            ItemStack itemstack = this.mainInventory.get(index);
            this.mainInventory.set(index, ItemStackTools.getEmptyStack());
            updateDisplayItems();
            return itemstack;
        }
        else
        {
            return ItemStackTools.getEmptyStack();
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
	public void setInventorySlotContents(int index, @Nonnull ItemStack stack)
    {
    	this.mainInventory.set(index, stack);
        updateDisplayItems();
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
    	ItemStackHelper.saveAllItems(nbt, mainInventory);
    	return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
    	clear();
    	ItemStackHelper.loadAllItems(nbt, mainInventory);
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
	public int getSizeInventory()
    {
        return this.mainInventory.size();
    }

    /**
     * Returns the stack in the given slot.
     */
    @Override
	public ItemStack getStackInSlot(int index)
    {
    	return this.mainInventory.get(index);
    }

    @Override
	public String getName()
    {
        return "container.inventory";
    }

    /**
     * Returns true if this thing is named
     */
    @Override
	public boolean hasCustomName()
    {
        return false;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    @Override
	public ITextComponent getDisplayName()
    {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]);
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    @Override
	public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    @Override
	public void markDirty()
    {
        this.inventoryChanged = true;
        updateDisplayItems();
    }

    @Override
	public boolean isUsableByPlayer(EntityPlayer player)
    {
        return player.isDead ? false : player.getDistanceSqToEntity(this.minion) <= 64.0D;
    }

    @Override
	public void openInventory(EntityPlayer player)
    {
    }

    @Override
	public void closeInventory(EntityPlayer player)
    {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    @Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
    {
    	if(index == 0) {
    		return ToolUtil.isSword(stack);
    	}
    	if(index == 1) {
    		return EntityMinionWarrior.isBow(stack);
    	}
    	if(index == 2){
    		return ItemStackTools.isValid(stack) && stack.getItem() instanceof ItemFood;
    	}
        return false;
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
        this.mainInventory.clear();
        updateDisplayItems();
    }

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.mainInventory)
        {
            if (ItemStackTools.isValid(itemstack))
            {
                return false;
            }
        }

        return true;
	}
}