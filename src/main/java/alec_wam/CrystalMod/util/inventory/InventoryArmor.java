package alec_wam.CrystalMod.util.inventory;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryArmor implements IInventory
{
	public EntityLivingBase entity;
    public boolean inventoryChanged;

    public InventoryArmor(EntityLivingBase entity)
    {
        this.entity = entity;
    }

    private EntityEquipmentSlot getSlotFromIndex(int index){
    	if(index == EntityEquipmentSlot.HEAD.getIndex()){
    		return EntityEquipmentSlot.HEAD;
    	}
    	if(index == EntityEquipmentSlot.CHEST.getIndex()){
    		return EntityEquipmentSlot.CHEST;
    	}
    	if(index == EntityEquipmentSlot.LEGS.getIndex()){
    		return EntityEquipmentSlot.LEGS;
    	}
    	if(index == EntityEquipmentSlot.FEET.getIndex()){
    		return EntityEquipmentSlot.FEET;
    	}
    	return null;
    }
    
    private ItemStack getArmorItem(int index) {
    	EntityEquipmentSlot slot = getSlotFromIndex(index);
    	if(slot !=null){
    		return entity.getItemStackFromSlot(slot);
    	}
		return ItemStackTools.getEmptyStack();
	}
    
    private void setArmorItem(int index, ItemStack stack) {
    	EntityEquipmentSlot slot = getSlotFromIndex(index);
    	if(slot !=null){
    		entity.setItemStackToSlot(slot, stack);
    	}
	}
    
    @Override
	public ItemStack decrStackSize(int index, int count)
    {
    	ItemStack stack = getArmorItem(index);
        if (ItemStackTools.isValid(stack))
        {
            if (ItemStackTools.getStackSize(stack) <= count)
            {
                ItemStack itemstack1 = stack;
                setArmorItem(index, ItemStackTools.getEmptyStack());
                return itemstack1;
            }
            else
            {
                ItemStack itemstack = stack.splitStack(count);

                if (ItemStackTools.isEmpty(itemstack))
                {
                	setArmorItem(index, ItemStackTools.getEmptyStack());
                }
                return itemstack;
            }
        }
        else
        {
            return ItemStackTools.getEmptyStack();
        }
    }

	@Override
	public ItemStack removeStackFromSlot(int index)
    {
    	if (ItemStackTools.isValid(getArmorItem(index)))
        {
            ItemStack itemstack = getArmorItem(index);
            setArmorItem(index, ItemStackTools.getEmptyStack());
            return itemstack;
        }
        else
        {
            return ItemStackTools.getEmptyStack();
        }
    }

    @Override
	public void setInventorySlotContents(int index, @Nonnull ItemStack stack)
    {
    	setArmorItem(index, stack);
    }

    @Override
	public int getSizeInventory()
    {
        return 4;
    }

    @Override
	public ItemStack getStackInSlot(int index)
    {
    	return getArmorItem(index);
    }

    @Override
	public String getName()
    {
        return "container.armor";
    }

    @Override
	public boolean hasCustomName()
    {
        return false;
    }

    @Override
	public ITextComponent getDisplayName()
    {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]);
    }

    @Override
	public int getInventoryStackLimit()
    {
        return 64;
    }

    public int getTotalArmorValue()
    {
        int i = 0;

        for (int j = 0; j < 4; ++j)
        {
            if (ItemStackTools.isValid(getArmorItem(j)) && getArmorItem(j).getItem() instanceof ItemArmor)
            {
                int k = ((ItemArmor)getArmorItem(j).getItem()).damageReduceAmount;
                i += k;
            }
        }

        return i;
    }

    public void damageArmor(float damage)
    {
        damage = damage / 4.0F;

        if (damage < 1.0F)
        {
            damage = 1.0F;
        }

        for (int j = 0; j < 4; ++j)
        {
            if (ItemStackTools.isValid(getArmorItem(j)) && getArmorItem(j).getItem() instanceof ItemArmor)
            {
            	getArmorItem(j).damageItem((int)damage, this.entity);

                if (ItemStackTools.isEmpty(getArmorItem(j)))
                {
                    setArmorItem(j, ItemStackTools.getEmptyStack());
                }
            }
        }
    }

    @Override
	public void markDirty()
    {
        this.inventoryChanged = true;
    }

    @Override
	public boolean isUsableByPlayer(EntityPlayer player)
    {
        return player.isDead ? false : player.getDistanceSqToEntity(this.entity) <= 64.0D;
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
        return ItemStackTools.isValid(stack) && stack.getItem() instanceof ItemArmor;
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
    	for(int i = 0; i < 4; i++){
    		setArmorItem(i, ItemStackTools.getEmptyStack());
    	}
    }

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack1 : this.entity.getArmorInventoryList())
        {
            if (ItemStackTools.isValid(itemstack1))
            {
                return false;
            }
        }

        return true;
	}
}