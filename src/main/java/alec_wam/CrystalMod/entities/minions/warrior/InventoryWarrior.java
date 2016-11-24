package alec_wam.CrystalMod.entities.minions.warrior;

import java.util.concurrent.Callable;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ReportedException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InventoryWarrior implements IInventory
{
	public static final int MAIN_SIZE = 10;
	public static final int ARMOR_SIZE = 4;
	
    public ItemStack[] mainInventory = new ItemStack[MAIN_SIZE];
    public ItemStack[] armorInventory = new ItemStack[ARMOR_SIZE];
    
    public int currentItem;
    public EntityMinionWarrior minion;
    public boolean inventoryChanged;

    public InventoryWarrior(EntityMinionWarrior minionIn)
    {
        this.minion = minionIn;
    }

    public ItemStack getCurrentItem()
    {
        return this.currentItem < 9 && this.currentItem >= 0 ? this.mainInventory[this.currentItem] : null;
    }

    private int getInventorySlotContainItem(Item itemIn)
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == itemIn)
            {
                return i;
            }
        }

        return -1;
    }

    @SideOnly(Side.CLIENT)
    private int getInventorySlotContainItemAndDamage(Item itemIn, int metadataIn)
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == itemIn && this.mainInventory[i].getMetadata() == metadataIn)
            {
                return i;
            }
        }

        return -1;
    }

    /**
     * stores an itemstack in the users inventory
     */
    private int storeItemStack(ItemStack itemStackIn)
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == itemStackIn.getItem() && this.mainInventory[i].isStackable() && this.mainInventory[i].stackSize < this.mainInventory[i].getMaxStackSize() && this.mainInventory[i].stackSize < this.getInventoryStackLimit() && (!this.mainInventory[i].getHasSubtypes() || this.mainInventory[i].getMetadata() == itemStackIn.getMetadata()) && ItemStack.areItemStackTagsEqual(this.mainInventory[i], itemStackIn))
            {
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns the first item stack that is empty.
     */
    public int getFirstEmptyStack()
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] == null)
            {
                return i;
            }
        }

        return -1;
    }

    @SideOnly(Side.CLIENT)
    public void setCurrentItem(Item itemIn, int metadataIn, boolean isMetaSpecific, boolean p_146030_4_)
    {
        ItemStack itemstack = this.getCurrentItem();
        int i = isMetaSpecific ? this.getInventorySlotContainItemAndDamage(itemIn, metadataIn) : this.getInventorySlotContainItem(itemIn);

        if (i >= 0 && i < 9)
        {
            this.currentItem = i;
        }
        else if (p_146030_4_ && itemIn != null)
        {
            int j = this.getFirstEmptyStack();

            if (j >= 0 && j < 9)
            {
                this.currentItem = j;
            }

            if (itemstack == null || !itemstack.isItemEnchantable() || this.getInventorySlotContainItemAndDamage(itemstack.getItem(), itemstack.getItemDamage()) != this.currentItem)
            {
                int k = this.getInventorySlotContainItemAndDamage(itemIn, metadataIn);
                int l;

                if (k >= 0)
                {
                    l = this.mainInventory[k].stackSize;
                    this.mainInventory[k] = this.mainInventory[this.currentItem];
                }
                else
                {
                    l = 1;
                }

                this.mainInventory[this.currentItem] = new ItemStack(itemIn, l, metadataIn);
            }
        }
    }

    /**
     * Removes matching items from the inventory.
     * @param itemIn The item to match, null ignores.
     * @param metadataIn The metadata to match, -1 ignores.
     * @param removeCount The number of items to remove. If less than 1, removes all matching items.
     * @param itemNBT The NBT data to match, null ignores.
     * @return The number of items removed from the inventory.
     */
    public int clearMatchingItems(Item itemIn, int metadataIn, int removeCount, NBTTagCompound itemNBT)
    {
        int i = 0;

        for (int j = 0; j < this.mainInventory.length; ++j)
        {
            ItemStack itemstack = this.mainInventory[j];

            if (ItemStackTools.isValid(itemstack) && (itemIn == null || itemstack.getItem() == itemIn) && (metadataIn <= -1 || itemstack.getMetadata() == metadataIn) && (itemNBT == null || NBTUtil.areNBTEquals(itemNBT, itemstack.getTagCompound(), true)))
            {
                int k = removeCount <= 0 ? ItemStackTools.getStackSize(itemstack) : Math.min(removeCount - i, ItemStackTools.getStackSize(itemstack));
                i += k;

                if (removeCount != 0)
                {
                    ItemStackTools.incStackSize(this.mainInventory[j], k);
                    
                    if (ItemStackTools.isEmpty(this.mainInventory[j]))
                    {
                        this.mainInventory[j] = ItemStackTools.getEmptyStack();
                    }

                    if (removeCount > 0 && i >= removeCount)
                    {
                        return i;
                    }
                }
            }
        }

        for (int l = 0; l < this.armorInventory.length; ++l)
        {
            ItemStack itemstack1 = this.armorInventory[l];

            if (itemstack1 != null && (itemIn == null || itemstack1.getItem() == itemIn) && (metadataIn <= -1 || itemstack1.getMetadata() == metadataIn) && (itemNBT == null || NBTUtil.areNBTEquals(itemNBT, itemstack1.getTagCompound(), false)))
            {
                int j1 = removeCount <= 0 ? itemstack1.stackSize : Math.min(removeCount - i, itemstack1.stackSize);
                i += j1;

                if (removeCount != 0)
                {
                    this.armorInventory[l].stackSize -= j1;

                    if (this.armorInventory[l].stackSize == 0)
                    {
                        this.armorInventory[l] = null;
                    }

                    if (removeCount > 0 && i >= removeCount)
                    {
                        return i;
                    }
                }
            }
        }

        return i;
    }

    /**
     * Switch the current item to the next one or the previous one
     *  
     * @param direction Direction to switch (1, 0, -1). 1 (any > 0) to select item left of current (decreasing
     * currentItem index), -1 (any < 0) to select item right of current (increasing currentItem index). 0 has no effect.
     */
    @SideOnly(Side.CLIENT)
    public void changeCurrentItem(int direction)
    {
        if (direction > 0)
        {
            direction = 1;
        }

        if (direction < 0)
        {
            direction = -1;
        }

        for (this.currentItem -= direction; this.currentItem < 0; this.currentItem += 9)
        {
            ;
        }

        while (this.currentItem >= 9)
        {
            this.currentItem -= 9;
        }
    }

    /**
     * This function stores as many items of an ItemStack as possible in a matching slot and returns the quantity of
     * left over items.
     */
    private int storePartialItemStack(ItemStack itemStackIn)
    {
        int i = itemStackIn.stackSize;
        int j = this.storeItemStack(itemStackIn);

        if (j < 0)
        {
            j = this.getFirstEmptyStack();
        }

        if (j < 0)
        {
            return i;
        }
        else
        {
            if (this.mainInventory[j] == null)
            {
                this.mainInventory[j] = itemStackIn.copy(); // Forge: Replace Item clone above to preserve item capabilities when picking the item up.
                this.mainInventory[j].stackSize = 0;
            }

            int k = i;

            if (i > this.mainInventory[j].getMaxStackSize() - this.mainInventory[j].stackSize)
            {
                k = this.mainInventory[j].getMaxStackSize() - this.mainInventory[j].stackSize;
            }

            if (k > this.getInventoryStackLimit() - this.mainInventory[j].stackSize)
            {
                k = this.getInventoryStackLimit() - this.mainInventory[j].stackSize;
            }

            if (k == 0)
            {
                return i;
            }
            else
            {
                i = i - k;
                this.mainInventory[j].stackSize += k;
                this.mainInventory[j].animationsToGo = 5;
                return i;
            }
        }
    }

    /**
     * Decrement the number of animations remaining. Only called on client side. This is used to handle the animation of
     * receiving a block.
     */
    public void decrementAnimations()
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null)
            {
                this.mainInventory[i].updateAnimation(this.minion.worldObj, this.minion, i, this.currentItem == i);
            }
        }

        /*for (int i = 0; i < armorInventory.length; i++)
        {
            if (armorInventory[i] != null)
            {
                armorInventory[i].getItem().onArmorTick(minion.worldObj, minion, armorInventory[i]);
            }
        }*/
    }

    /**
     * removed one item of specified Item from inventory (if it is in a stack, the stack size will reduce with 1)
     */
    public boolean consumeInventoryItem(Item itemIn)
    {
        int i = this.getInventorySlotContainItem(itemIn);

        if (i < 0)
        {
            return false;
        }
        else
        {
            if (--this.mainInventory[i].stackSize <= 0)
            {
                this.mainInventory[i] = null;
            }

            return true;
        }
    }

    /**
     * Checks if a specified Item is inside the inventory
     */
    public boolean hasItem(Item itemIn)
    {
        int i = this.getInventorySlotContainItem(itemIn);
        return i >= 0;
    }

    /**
     * Adds the item stack to the inventory, returns false if it is impossible.
     */
    public boolean addItemStackToInventory(final ItemStack itemStackIn)
    {
        if (ItemStackTools.isValid(itemStackIn) && itemStackIn.getItem() != null)
        {
            try
            {
                if (itemStackIn.isItemDamaged())
                {
                    int j = this.getFirstEmptyStack();

                    if (j >= 0)
                    {
                        this.mainInventory[j] = ItemStack.copyItemStack(itemStackIn);
                        this.mainInventory[j].animationsToGo = 5;
                        ItemStackTools.makeEmpty(itemStackIn);
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    int i;

                    while (true)
                    {
                        i = ItemStackTools.getStackSize(itemStackIn);
                        ItemStackTools.setStackSize(itemStackIn, this.storePartialItemStack(itemStackIn));

                        if (ItemStackTools.isEmpty(itemStackIn) || ItemStackTools.getStackSize(itemStackIn) >= i)
                        {
                            break;
                        }
                    }

                    return ItemStackTools.getStackSize(itemStackIn) < i;
                }
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                crashreportcategory.addCrashSection("Item ID", Integer.valueOf(Item.getIdFromItem(itemStackIn.getItem())));
                crashreportcategory.addCrashSection("Item data", Integer.valueOf(itemStackIn.getMetadata()));
                crashreportcategory.addCrashSection("Item name", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        return itemStackIn.getDisplayName();
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack[] aitemstack = this.mainInventory;

        if (index >= this.mainInventory.length)
        {
            aitemstack = this.armorInventory;
            index -= this.mainInventory.length;
        }

        if (aitemstack[index] != null)
        {
            if (aitemstack[index].stackSize <= count)
            {
                ItemStack itemstack1 = aitemstack[index];
                aitemstack[index] = null;
                return itemstack1;
            }
            else
            {
                ItemStack itemstack = aitemstack[index].splitStack(count);

                if (aitemstack[index].stackSize == 0)
                {
                    aitemstack[index] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    public ItemStack removeStackFromSlot(int index)
    {
        ItemStack[] aitemstack = this.mainInventory;

        if (index >= this.mainInventory.length)
        {
            aitemstack = this.armorInventory;
            index -= this.mainInventory.length;
        }

        if (aitemstack[index] != null)
        {
            ItemStack itemstack = aitemstack[index];
            aitemstack[index] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        ItemStack[] aitemstack = this.mainInventory;

        if (index >= aitemstack.length)
        {
            index -= aitemstack.length;
            aitemstack = this.armorInventory;
        }

        aitemstack[index] = stack;
    }

    public float getStrVsBlock(IBlockState blockIn)
    {
        float f = 1.0F;

        if (this.mainInventory[this.currentItem] != null)
        {
            f *= this.mainInventory[this.currentItem].getStrVsBlock(blockIn);
        }

        return f;
    }

    /**
     * Writes the inventory out as a list of compound tags. This is where the slot indices are used (+100 for armor, +80
     * for crafting).
     *  
     * @param nbtTagListIn List to append tags to
     */
    public NBTTagList writeToNBT(NBTTagList nbtTagListIn)
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                this.mainInventory[i].writeToNBT(nbttagcompound);
                nbtTagListIn.appendTag(nbttagcompound);
            }
        }

        for (int j = 0; j < this.armorInventory.length; ++j)
        {
            if (this.armorInventory[j] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)(j + 100));
                this.armorInventory[j].writeToNBT(nbttagcompound1);
                nbtTagListIn.appendTag(nbttagcompound1);
            }
        }

        return nbtTagListIn;
    }

    /**
     * Reads from the given tag list and fills the slots in the inventory with the correct items.
     *  
     * @param nbtTagListIn tagList to read from
     */
    public void readFromNBT(NBTTagList nbtTagListIn)
    {
        this.mainInventory = new ItemStack[MAIN_SIZE];
        this.armorInventory = new ItemStack[ARMOR_SIZE];

        for (int i = 0; i < nbtTagListIn.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbtTagListIn.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);

            if (itemstack != null)
            {
                if (j >= 0 && j < this.mainInventory.length)
                {
                    this.mainInventory[j] = itemstack;
                }

                if (j >= 100 && j < this.armorInventory.length + 100)
                {
                    this.armorInventory[j - 100] = itemstack;
                }
            }
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.mainInventory.length + ARMOR_SIZE;
    }

    /**
     * Returns the stack in the given slot.
     */
    public ItemStack getStackInSlot(int index)
    {
        ItemStack[] aitemstack = this.mainInventory;

        if (index >= aitemstack.length)
        {
            index -= aitemstack.length;
            aitemstack = this.armorInventory;
        }

        return aitemstack[index];
    }

    public String getName()
    {
        return "container.inventory";
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return false;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    public ITextComponent getDisplayName()
    {
        return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    public boolean canHeldItemHarvest(IBlockState blockIn)
    {
        if (blockIn.getMaterial().isToolNotRequired())
        {
            return true;
        }
        else
        {
            ItemStack itemstack = this.getStackInSlot(this.currentItem);
            return itemstack != null ? itemstack.canHarvestBlock(blockIn) : false;
        }
    }

    public ItemStack armorItemInSlot(int slotIn)
    {
        return this.armorInventory[slotIn];
    }

    /**
     * Based on the damage values and maximum damage values of each armor item, returns the current armor value.
     */
    public int getTotalArmorValue()
    {
        int i = 0;

        for (int j = 0; j < this.armorInventory.length; ++j)
        {
            if (this.armorInventory[j] != null && this.armorInventory[j].getItem() instanceof ItemArmor)
            {
                int k = ((ItemArmor)this.armorInventory[j].getItem()).damageReduceAmount;
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

        for (int i = 0; i < this.armorInventory.length; ++i)
        {
            if (this.armorInventory[i] != null && this.armorInventory[i].getItem() instanceof ItemArmor)
            {
                this.armorInventory[i].damageItem((int)damage, this.minion);

                if (this.armorInventory[i].stackSize == 0)
                {
                    this.armorInventory[i] = null;
                }
            }
        }
    }

    /**
     * Drop all armor and main inventory items.
     */
    public void dropAllItems()
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null)
            {
                this.minion.dropItem(this.mainInventory[i], true, false);
                this.mainInventory[i] = null;
            }
        }

        for (int j = 0; j < this.armorInventory.length; ++j)
        {
            if (this.armorInventory[j] != null)
            {
                this.minion.dropItem(this.armorInventory[j], true, false);
                this.armorInventory[j] = null;
            }
        }
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty()
    {
        this.inventoryChanged = true;
    }

    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return player.isDead ? false : player.getDistanceSqToEntity(this.minion) <= 64.0D;
    }

    /**
     * Returns true if the specified ItemStack exists in the inventory.
     */
    public boolean hasItemStack(ItemStack itemStackIn)
    {
        for (int i = 0; i < this.armorInventory.length; ++i)
        {
            if (this.armorInventory[i] != null && this.armorInventory[i].isItemEqual(itemStackIn))
            {
                return true;
            }
        }

        for (int j = 0; j < this.mainInventory.length; ++j)
        {
            if (this.mainInventory[j] != null && this.mainInventory[j].isItemEqual(itemStackIn))
            {
                return true;
            }
        }

        return false;
    }

    public void openInventory(EntityPlayer player)
    {
    }

    public void closeInventory(EntityPlayer player)
    {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    /**
     * Copy the ItemStack contents from another InventoryPlayer instance
     */
    public void copyInventory(InventoryWarrior minionInventory)
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            this.mainInventory[i] = ItemStack.copyItemStack(minionInventory.mainInventory[i]);
        }

        for (int j = 0; j < this.armorInventory.length; ++j)
        {
            this.armorInventory[j] = ItemStack.copyItemStack(minionInventory.armorInventory[j]);
        }

        this.currentItem = minionInventory.currentItem;
    }

    public int getField(int id)
    {
        return 0;
    }

    public void setField(int id, int value)
    {
    }

    public int getFieldCount()
    {
        return 0;
    }

    public void clear()
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            this.mainInventory[i] = null;
        }

        for (int j = 0; j < this.armorInventory.length; ++j)
        {
            this.armorInventory[j] = null;
        }
    }
}