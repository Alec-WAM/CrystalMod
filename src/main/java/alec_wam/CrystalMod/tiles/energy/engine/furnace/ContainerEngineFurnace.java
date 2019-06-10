package alec_wam.CrystalMod.tiles.energy.engine.furnace;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ContainerEngineFurnace extends Container
{
    public TileEntityEngineFurnace tileFurnace;

    public ContainerEngineFurnace(int windowId, PlayerEntity player, TileEntityEngineFurnace tileEntity)
    {
    	super(null, windowId);
    	this.tileFurnace = (tileEntity);

        this.addSlot(new Slot(tileFurnace, 0, 54, 45));
    	this.addPlayerInventory(player.inventory);
    }

    @Override
    public boolean canInteractWith(PlayerEntity player)
    {
        return (this.tileFurnace != null && this.tileFurnace.isUsableByPlayer(player));
    }

    protected void addPlayerInventory(PlayerInventory paramPlayerInventory)
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(paramPlayerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(paramPlayerInventory, i, 8 + i * 18, 142));
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int i)
    {
    	ItemStack itemstack = ItemStackTools.getEmptyStack();
    	int par2 = i;
		Slot slot = this.inventorySlots.get(par2);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (par2 == 0)
			{
				if (!this.mergeItemStack(itemstack1, 1, 38, true))
				{
					return ItemStackTools.getEmptyStack();
				}

				slot.onSlotChange(itemstack1, itemstack);
			}
			else if (par2 != 0)
			{
				if (TileEntityEngineFurnace.getItemEnergyValue(itemstack1) > 0)
				{
					if (!this.mergeItemStack(itemstack1, 0, 1, false))
					{
						
						return ItemStackTools.getEmptyStack();
					}
				}
				else if (par2 >= 1 && par2 < 30)
				{
					if (!this.mergeItemStack(itemstack1, 30, 38, false))
					{
						return ItemStackTools.getEmptyStack();
					}
				}
				else if (par2 >= 30 && par2 < 38 && !this.mergeItemStack(itemstack1, 1, 30, false))
				{
					return ItemStackTools.getEmptyStack();
				}
			}
			else if (!this.mergeItemStack(itemstack1, 1, 38, false))
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
