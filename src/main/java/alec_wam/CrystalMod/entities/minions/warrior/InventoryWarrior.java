package alec_wam.CrystalMod.entities.minions.warrior;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryWarrior implements IInventory
{
	public static final int MAIN_SIZE = 10;
	public static final int ARMOR_SIZE = 4;
	
	public NonNullList<ItemStack> mainInventory = NonNullList.<ItemStack>withSize(MAIN_SIZE, ItemStack.EMPTY);
	public NonNullList<ItemStack> armorInventory = NonNullList.<ItemStack>withSize(ARMOR_SIZE, ItemStack.EMPTY);
    
    public int currentItem;
    public EntityMinionWarrior minion;
    public boolean inventoryChanged;

    public InventoryWarrior(EntityMinionWarrior minionIn)
    {
        this.minion = minionIn;
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    @Override
	public ItemStack decrStackSize(int index, int count)
    {
    	NonNullList<ItemStack> aitemstack = this.mainInventory;

        if (index >= aitemstack.size())
        {
            index -= aitemstack.size();
            aitemstack = this.armorInventory;
        }

        ItemStack stack = aitemstack.get(index);
        if (ItemStackTools.isValid(stack))
        {
            if (ItemStackTools.getStackSize(stack) <= count)
            {
                ItemStack itemstack1 = stack;
                aitemstack.set(index, ItemStackTools.getEmptyStack());
                return itemstack1;
            }
            else
            {
                ItemStack itemstack = stack.splitStack(count);

                if (ItemStackTools.isEmpty(itemstack))
                {
                	aitemstack.set(index, ItemStackTools.getEmptyStack());
                }

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
    	NonNullList<ItemStack> aitemstack = this.mainInventory;

        if (index >= aitemstack.size())
        {
            index -= aitemstack.size();
            aitemstack = this.armorInventory;
        }

        if (ItemStackTools.isValid(aitemstack.get(index)))
        {
            ItemStack itemstack = aitemstack.get(index);
            aitemstack.set(index, ItemStackTools.getEmptyStack());
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
    	NonNullList<ItemStack> aitemstack = this.mainInventory;

        if (index >= aitemstack.size())
        {
            index -= aitemstack.size();
            aitemstack = this.armorInventory;
        }

        aitemstack.set(index, stack);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
    	ItemStackHelper.saveAllItems(nbt, mainInventory);
    	ItemStackHelper.saveAllItems(nbt, armorInventory);
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
    	ItemStackHelper.loadAllItems(nbt, mainInventory);
    	ItemStackHelper.loadAllItems(nbt, armorInventory);
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
	public int getSizeInventory()
    {
        return this.mainInventory.size() + ARMOR_SIZE;
    }

    /**
     * Returns the stack in the given slot.
     */
    @Override
	public ItemStack getStackInSlot(int index)
    {
    	NonNullList<ItemStack> aitemstack = this.mainInventory;

        if (index >= aitemstack.size())
        {
            index -= aitemstack.size();
            aitemstack = this.armorInventory;
        }

        return aitemstack.get(index);
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

    public ItemStack armorItemInSlot(int slotIn)
    {
        return this.armorInventory.get(slotIn);
    }

    /**
     * Based on the damage values and maximum damage values of each armor item, returns the current armor value.
     */
    public int getTotalArmorValue()
    {
        int i = 0;

        for (int j = 0; j < this.armorInventory.size(); ++j)
        {
            if (ItemStackTools.isValid(armorItemInSlot(j)) && armorItemInSlot(j).getItem() instanceof ItemArmor)
            {
                int k = ((ItemArmor)armorItemInSlot(j).getItem()).damageReduceAmount;
                i += k;
            }
        }

        return i;
    }

    /**
     * Damages armor in each slot by the specified amount.
     */
    public void damageArmor(float damage)
    {
        damage = damage / 4.0F;

        if (damage < 1.0F)
        {
            damage = 1.0F;
        }

        for (int j = 0; j < this.armorInventory.size(); ++j)
        {
            if (ItemStackTools.isValid(armorItemInSlot(j)) && armorItemInSlot(j).getItem() instanceof ItemArmor)
            {
            	armorItemInSlot(j).damageItem((int)damage, this.minion);

                if (ItemStackTools.isEmpty(armorItemInSlot(j)))
                {
                    this.armorInventory.set(j, ItemStackTools.getEmptyStack());
                }
            }
        }
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    @Override
	public void markDirty()
    {
        this.inventoryChanged = true;
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
        this.mainInventory.clear();
        this.armorInventory.clear();
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

        for (ItemStack itemstack1 : this.armorInventory)
        {
            if (ItemStackTools.isValid(itemstack1))
            {
                return false;
            }
        }

        return true;
	}
}