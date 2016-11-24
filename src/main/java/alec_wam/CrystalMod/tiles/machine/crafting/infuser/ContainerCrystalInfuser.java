package alec_wam.CrystalMod.tiles.machine.crafting.infuser;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;

public class ContainerCrystalInfuser extends Container
{
    public TileEntityCrystalInfuser tileMachine;

    public ContainerCrystalInfuser(EntityPlayer player, TileEntityCrystalInfuser tileEntity)
    {
        this.addPlayerInventory(player.inventory);
        
        this.tileMachine = ((TileEntityCrystalInfuser) tileEntity);

        this.addSlotToContainer(new Slot(tileMachine, 0, 132, 34));
        this.addSlotToContainer(new SlotFurnaceOutput(player, tileMachine, 1, 80, 34));
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
    	ItemStack itemstack = ItemStackTools.getEmptyStack();
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
					return ItemStackTools.getEmptyStack();
				}

				slot.onSlotChange(itemstack1, itemstack);
			}
			else if (par2 != 36)/*not input*/
			{
				if (CrystalInfusionManager.isInput(itemstack1))
				{
					if (!this.mergeItemStack(itemstack1, 36, 37, false))
					{
						return ItemStackTools.getEmptyStack();
					}
				}
				else if (par2 >= 0 && par2 < 27)
				{
					if (!this.mergeItemStack(itemstack1, 27, 36, false))
					{
						return ItemStackTools.getEmptyStack();
					}
				}
				else if (par2 >= 27 && par2 < 36 && !this.mergeItemStack(itemstack1, 0, 27, false))
				{
					return ItemStackTools.getEmptyStack();
				}
			}
			/*input*/
			else if (!this.mergeItemStack(itemstack1, 0, 36, false))
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

			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
    }
}
