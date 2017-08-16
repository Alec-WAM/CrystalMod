package alec_wam.CrystalMod.tiles.machine.power.engine.lava;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerEngineLava extends Container
{
    public TileEntityEngineLava tileLava;

    public ContainerEngineLava(EntityPlayer player, TileEntityEngineLava tileEntity)
    {
    	this.tileLava = (tileEntity);
    	this.addPlayerInventory(player.inventory);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
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
    	ItemStack itemstack = ItemStackTools.getEmptyStack();
    	int par2 = i;
		Slot slot = this.inventorySlots.get(par2);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (par2 != 0)
			{
				if (par2 >= 0 && par2 < 30)
				{
					if (!this.mergeItemStack(itemstack1, 30, 38, false))
					{
						return ItemStackTools.getEmptyStack();
					}
				}
				else if (par2 >= 30 && par2 < 38 && !this.mergeItemStack(itemstack1, 0, 30, false))
				{
					return ItemStackTools.getEmptyStack();
				}
			}
			else if (!this.mergeItemStack(itemstack1, 0, 38, false))
			{
				return ItemStackTools.getEmptyStack();
			}

			if (ItemStackTools.isEmpty(itemstack1))
			{
				slot.putStack(ItemStackTools.getEmptyStack());
			}
			else
			{
				slot.onSlotChanged();
			}

			if (ItemStackTools.getStackSize(itemstack1) == ItemStackTools.getStackSize(itemstack))
			{
				return ItemStackTools.getEmptyStack();
			}

			slot.onTake(player, itemstack1);
		}

		return itemstack;
    }
}
