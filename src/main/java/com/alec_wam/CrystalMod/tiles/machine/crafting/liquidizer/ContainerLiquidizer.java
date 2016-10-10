package com.alec_wam.CrystalMod.tiles.machine.crafting.liquidizer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;

public class ContainerLiquidizer extends Container
{
    public TileEntityLiquidizer tileMachine;

    public ContainerLiquidizer(EntityPlayer player, TileEntityLiquidizer tileEntity)
    {
        this.addPlayerInventory(player.inventory);
        
        this.tileMachine = tileEntity;

        this.addSlotToContainer(new Slot(tileMachine, 0, 56, 35));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return (this.tileMachine != null && this.tileMachine.isUseableByPlayer(player));
    }

    protected void addPlayerInventory(InventoryPlayer paramInventoryPlayer)
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                this.addSlotToContainer(new Slot(paramInventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++)
            this.addSlotToContainer(new Slot(paramInventoryPlayer, i, 8 + i * 18, 142));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i)
    {
    	ItemStack itemstack = null;
    	int par2 = i;
		Slot slot = (Slot)this.inventorySlots.get(par2);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			/*output*/
			if (par2 == 37)
			{
				if (!this.mergeItemStack(itemstack1, 0, 35, false))
				{
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			}
			else if (par2 != 36)/*not input*/
			{
				if (LiquidizerRecipeManager.getRecipe(itemstack1) !=null)
				{
					if (!this.mergeItemStack(itemstack1, 36, 37, false))
					{
						return null;
					}
				}
				else if (par2 >= 0 && par2 < 27)
				{
					if (!this.mergeItemStack(itemstack1, 27, 36, false))
					{
						return null;
					}
				}
				else if (par2 >= 27 && par2 < 36 && !this.mergeItemStack(itemstack1, 0, 27, false))
				{
					return null;
				}
			}
			/*input*/
			else if (!this.mergeItemStack(itemstack1, 0, 36, false))
			{
				return null;
			}

			if (itemstack1.stackSize == 0)
			{
				slot.putStack((ItemStack)null);
			}
			else
			{
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize)
			{
				return null;
			}

			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
    }
}
